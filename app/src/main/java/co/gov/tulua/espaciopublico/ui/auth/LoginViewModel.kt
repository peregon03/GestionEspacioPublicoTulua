package co.gov.tulua.espaciopublico.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.gov.tulua.espaciopublico.data.local.session.SessionManager
import co.gov.tulua.espaciopublico.domain.model.AuthCredentials
import co.gov.tulua.espaciopublico.domain.model.User
import co.gov.tulua.espaciopublico.domain.model.UserRole
import co.gov.tulua.espaciopublico.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface LoginUiState {
    data object Idle : LoginUiState
    data object Loading : LoginUiState
    data class Success(val user: User) : LoginUiState
    data class Error(val message: String) : LoginUiState
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    val emailOrCedula = MutableStateFlow("")
    val password = MutableStateFlow("")
    val selectedRole = MutableStateFlow(UserRole.INSPECTOR)
    val passwordVisible = MutableStateFlow(false)

    // El botón se habilita solo cuando hay datos básicos
    val isFormValid: StateFlow<Boolean> get() = _isFormValid
    private val _isFormValid = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            combine(
                emailOrCedula,
                password,
            ) { identifier, pass ->
                identifier.isNotBlank() && pass.length >= 6
            }.collect { valid ->
                _isFormValid.value = valid
            }
        }
    }

    fun togglePasswordVisibility() {
        passwordVisible.value = !passwordVisible.value
    }

    fun login() {
        if (_uiState.value is LoginUiState.Loading) return

        val identifier = emailOrCedula.value.trim()
        val pass = password.value

        if (identifier.isBlank()) {
            _uiState.value = LoginUiState.Error("Ingrese su correo o cédula")
            return
        }
        if (pass.length < 6) {
            _uiState.value = LoginUiState.Error("La contraseña debe tener al menos 6 caracteres")
            return
        }

        _uiState.value = LoginUiState.Loading

        viewModelScope.launch {
            val credentials = AuthCredentials(
                identifier = identifier,
                password = pass,
                role = selectedRole.value,
            )
            authRepository.login(credentials)
                .onSuccess { response ->
                    sessionManager.saveSession(
                        user = response.user,
                        token = response.token,
                        refreshToken = response.refreshToken,
                    )
                    _uiState.value = LoginUiState.Success(response.user)
                }
                .onFailure { error ->
                    _uiState.value = LoginUiState.Error(
                        error.message ?: "Error al iniciar sesión"
                    )
                }
        }
    }

    fun clearError() {
        if (_uiState.value is LoginUiState.Error) {
            _uiState.value = LoginUiState.Idle
        }
    }
}
