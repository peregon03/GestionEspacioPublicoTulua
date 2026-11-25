package co.gov.tulua.espaciopublico.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.gov.tulua.espaciopublico.domain.model.DashboardMetrics
import co.gov.tulua.espaciopublico.domain.model.Sector
import co.gov.tulua.espaciopublico.domain.repository.DashboardRepository
import co.gov.tulua.espaciopublico.domain.repository.SectorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface DashboardUiState {
    data object Loading : DashboardUiState
    data class Success(
        val metrics: DashboardMetrics,
        val sectors: List<Sector>,
    ) : DashboardUiState
    data class Error(val message: String) : DashboardUiState
}

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val dashboardRepository: DashboardRepository,
    private val sectorRepository: SectorRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun refresh() {
        _uiState.value = DashboardUiState.Loading
        load()
    }

    private fun load() {
        viewModelScope.launch {
            // Carga en paralelo métricas y sectores
            val metricsDeferred = async { dashboardRepository.getMetrics() }
            val sectorsDeferred = async { sectorRepository.getSectors() }

            val metricsResult = metricsDeferred.await()
            val sectorsResult = sectorsDeferred.await()

            if (metricsResult.isFailure) {
                _uiState.value = DashboardUiState.Error(
                    metricsResult.exceptionOrNull()?.message
                        ?: "Error al cargar el resumen"
                )
                return@launch
            }

            if (sectorsResult.isFailure) {
                _uiState.value = DashboardUiState.Error(
                    sectorsResult.exceptionOrNull()?.message
                        ?: "Error al cargar los sectores"
                )
                return@launch
            }

            _uiState.value = DashboardUiState.Success(
                metrics = metricsResult.getOrThrow(),
                sectors = sectorsResult.getOrThrow(),
            )
        }
    }
}
