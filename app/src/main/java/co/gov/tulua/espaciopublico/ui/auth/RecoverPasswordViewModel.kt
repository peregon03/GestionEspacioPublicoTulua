package co.gov.tulua.espaciopublico.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.gov.tulua.espaciopublico.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface RecoverPasswordUiState {
    data object Idle : RecoverPasswordUiState
    data object Loading : RecoverPasswordUiState
    data object Success : RecoverPasswordUiState
    data class Error(val message: String) : RecoverPasswordUiState
}

@HiltViewModel
class RecoverPasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<RecoverPasswordUiState>(RecoverPasswordUiState.Idle)
    val uiState: StateFlow<RecoverPasswordUiState> = _uiState.asStateFlow()

    fun sendRecoveryEmail(email: String) {
        if (_uiState.value is RecoverPasswordUiState.Loading) return

        val trimmed = email.trim()
        if (trimmed.isBlank() || !trimmed.contains("@")) {
            _uiState.value = RecoverPasswordUiState.Error("Ingrese un correo electrónico válido")
            return
        }

        _uiState.value = RecoverPasswordUiState.Loading

        viewModelScope.launch {
            authRepository.forgotPassword(trimmed)
                .onSuccess {
                    _uiState.value = RecoverPasswordUiState.Success
                }
                .onFailure { error ->
                    _uiState.value = RecoverPasswordUiState.Error(
                        error.message ?: "Error al enviar el enlace"
                    )
                }
        }
    }

    fun clearError() {
        if (_uiState.value is RecoverPasswordUiState.Error) {
            _uiState.value = RecoverPasswordUiState.Idle
        }
    }

    fun reset() {
        _uiState.value = RecoverPasswordUiState.Idle
    }
}
