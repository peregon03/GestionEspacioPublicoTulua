package co.gov.tulua.espaciopublico.ui.census

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import co.gov.tulua.espaciopublico.domain.model.Module
import co.gov.tulua.espaciopublico.domain.repository.ModuleRepository
import co.gov.tulua.espaciopublico.ui.navigation.AppDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ModuleDetailUiState {
    data object Loading : ModuleDetailUiState
    data class Success(val module: Module) : ModuleDetailUiState
    data class Error(val message: String) : ModuleDetailUiState
}

@HiltViewModel
class ModuleDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val moduleRepository: ModuleRepository,
) : ViewModel() {

    private val moduleId: Long = savedStateHandle.toRoute<AppDestination.ModuleDetail>().moduleId

    private val _uiState = MutableStateFlow<ModuleDetailUiState>(ModuleDetailUiState.Loading)
    val uiState: StateFlow<ModuleDetailUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun retry() {
        _uiState.value = ModuleDetailUiState.Loading
        load()
    }

    private fun load() {
        viewModelScope.launch {
            val result = moduleRepository.getModuleById(moduleId)
            _uiState.value = if (result.isSuccess) {
                ModuleDetailUiState.Success(result.getOrThrow())
            } else {
                ModuleDetailUiState.Error(
                    result.exceptionOrNull()?.message ?: "Error al cargar el modulo"
                )
            }
        }
    }
}
