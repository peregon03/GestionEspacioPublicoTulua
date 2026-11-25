package co.gov.tulua.espaciopublico.ui.map

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.gov.tulua.espaciopublico.core.designsystem.Amber500
import co.gov.tulua.espaciopublico.core.designsystem.Green900
import co.gov.tulua.espaciopublico.core.map.LatLng
import co.gov.tulua.espaciopublico.core.map.MapMarker
import co.gov.tulua.espaciopublico.core.map.MapPolygon
import co.gov.tulua.espaciopublico.core.map.toLatLng
import co.gov.tulua.espaciopublico.domain.model.Module
import co.gov.tulua.espaciopublico.domain.model.ModuleStatus
import co.gov.tulua.espaciopublico.domain.model.ModuleType
import co.gov.tulua.espaciopublico.domain.model.Sector
import co.gov.tulua.espaciopublico.domain.model.SaturationLevel
import co.gov.tulua.espaciopublico.domain.repository.ModuleRepository
import co.gov.tulua.espaciopublico.domain.repository.SectorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Color distintivo para módulos de tipo Feria (índigo claro). */
private val ColorFeria = Color(0xFF5C6BC0)

/** Relleno semitransparente según nivel de saturación del sector. */
private fun SaturationLevel.fillColor(): Color = when (this) {
    SaturationLevel.BAJA    -> Color(0x332E7D32)
    SaturationLevel.MEDIA   -> Color(0x33F5B14C)
    SaturationLevel.ALTA    -> Color(0x33E65100)
    SaturationLevel.CRITICA -> Color(0x33C62828)
}

/** Trazo opaco (mismo tono sin alfa) según nivel de saturación. */
private fun SaturationLevel.strokeColor(): Color = when (this) {
    SaturationLevel.BAJA    -> Color(0xFF2E7D32)
    SaturationLevel.MEDIA   -> Color(0xFFF5B14C)
    SaturationLevel.ALTA    -> Color(0xFFE65100)
    SaturationLevel.CRITICA -> Color(0xFFC62828)
}

/** Polígonos aproximados para cada sector, derivados de las coordenadas reales del mock. */
private val SECTOR_POLYGONS: Map<Long, List<LatLng>> = mapOf(
    // Sector 1: Carrera 25 (lat ~4.0855–4.0871, lon ~-76.1958–-76.1970)
    1L to listOf(
        LatLng(4.0875, -76.1973),
        LatLng(4.0875, -76.1955),
        LatLng(4.0853, -76.1955),
        LatLng(4.0853, -76.1973),
    ),
    // Sector 2: Galería y Plaza de Mercado (lat ~4.0846–4.0853, lon ~-76.1950–-76.1957)
    2L to listOf(
        LatLng(4.0856, -76.1960),
        LatLng(4.0856, -76.1947),
        LatLng(4.0844, -76.1947),
        LatLng(4.0844, -76.1960),
    ),
    // Sector 3: Parque Boyacá (lat ~4.0837–4.0844, lon ~-76.1942–-76.1949)
    3L to listOf(
        LatLng(4.0847, -76.1952),
        LatLng(4.0847, -76.1939),
        LatLng(4.0834, -76.1939),
        LatLng(4.0834, -76.1952),
    ),
    // Sector 4: Parque Céspedes (lat ~4.0830–4.0835, lon ~-76.1936–-76.1941)
    4L to listOf(
        LatLng(4.0838, -76.1944),
        LatLng(4.0838, -76.1933),
        LatLng(4.0828, -76.1933),
        LatLng(4.0828, -76.1944),
    ),
    // Sector 5: Plaza de Mercado Central (lat ~4.0820–4.0829, lon ~-76.1925–-76.1934)
    5L to listOf(
        LatLng(4.0832, -76.1937),
        LatLng(4.0832, -76.1922),
        LatLng(4.0818, -76.1922),
        LatLng(4.0818, -76.1937),
    ),
)

data class MapUiState(
    val modules: List<Module> = emptyList(),
    val sectors: List<Sector> = emptyList(),
    val markers: List<MapMarker> = emptyList(),
    val polygons: List<MapPolygon> = emptyList(),
    val selectedModule: Module? = null,
    val searchQuery: String = "",
    val filterType: ModuleType? = null,
    val filterStatus: ModuleStatus? = null,
    val filterSectorId: Long? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class MapViewModel @Inject constructor(
    private val moduleRepository: ModuleRepository,
    private val sectorRepository: SectorRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState(isLoading = true))
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    init {
        cargarDatos()
    }

    private fun cargarDatos() {
        viewModelScope.launch {
            val modulesDeferred = async { moduleRepository.getModulesForMap() }
            val sectorsDeferred = async { sectorRepository.getSectors() }

            val modulesResult = modulesDeferred.await()
            val sectorsResult = sectorsDeferred.await()

            if (modulesResult.isFailure) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = modulesResult.exceptionOrNull()?.message
                            ?: "Error al cargar los modulos",
                    )
                }
                return@launch
            }

            if (sectorsResult.isFailure) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = sectorsResult.exceptionOrNull()?.message
                            ?: "Error al cargar los sectores",
                    )
                }
                return@launch
            }

            val modules = modulesResult.getOrThrow()
            val sectors = sectorsResult.getOrThrow()
            val polygons = construirPoligonos(sectors)

            _uiState.update {
                it.copy(
                    modules = modules,
                    sectors = sectors,
                    markers = construirMarcadores(modules, it.searchQuery, it.filterType, it.filterStatus, it.filterSectorId),
                    polygons = polygons,
                    isLoading = false,
                    error = null,
                )
            }
        }
    }

    /** Selecciona un módulo para mostrar su bottom sheet. */
    fun selectModule(moduleId: String) {
        val id = moduleId.toLongOrNull() ?: return
        val module = _uiState.value.modules.find { it.id == id }
        _uiState.update { it.copy(selectedModule = module) }
    }

    /** Cierra el bottom sheet de detalle. */
    fun clearSelection() {
        _uiState.update { it.copy(selectedModule = null) }
    }

    /** Actualiza la búsqueda y recalcula los marcadores visibles. */
    fun updateSearch(query: String) {
        _uiState.update { state ->
            state.copy(
                searchQuery = query,
                markers = construirMarcadores(
                    state.modules,
                    query,
                    state.filterType,
                    state.filterStatus,
                    state.filterSectorId,
                ),
            )
        }
    }

    /** Actualiza el filtro de tipo y recalcula los marcadores visibles. */
    fun updateFilterType(type: ModuleType?) {
        _uiState.update { state ->
            state.copy(
                filterType = type,
                markers = construirMarcadores(
                    state.modules,
                    state.searchQuery,
                    type,
                    state.filterStatus,
                    state.filterSectorId,
                ),
            )
        }
    }

    /** Actualiza el filtro de estado y recalcula los marcadores visibles. */
    fun updateFilterStatus(status: ModuleStatus?) {
        _uiState.update { state ->
            state.copy(
                filterStatus = status,
                markers = construirMarcadores(
                    state.modules,
                    state.searchQuery,
                    state.filterType,
                    status,
                    state.filterSectorId,
                ),
            )
        }
    }

    /** Actualiza el filtro de sector y recalcula los marcadores visibles. */
    fun updateFilterSector(sectorId: Long?) {
        _uiState.update { state ->
            state.copy(
                filterSectorId = sectorId,
                markers = construirMarcadores(
                    state.modules,
                    state.searchQuery,
                    state.filterType,
                    state.filterStatus,
                    sectorId,
                ),
            )
        }
    }

    /** Limpia todos los filtros avanzados (mantiene la búsqueda). */
    fun clearAdvancedFilters() {
        _uiState.update { state ->
            state.copy(
                filterStatus = null,
                filterSectorId = null,
                markers = construirMarcadores(
                    state.modules,
                    state.searchQuery,
                    state.filterType,
                    null,
                    null,
                ),
            )
        }
    }

    // ── Helpers privados ──────────────────────────────────────────────────────

    private fun construirMarcadores(
        modules: List<Module>,
        query: String,
        type: ModuleType?,
        status: ModuleStatus?,
        sectorId: Long?,
    ): List<MapMarker> =
        modules
            .filter { m ->
                (query.isBlank() ||
                    m.name.contains(query, ignoreCase = true) ||
                    m.code.contains(query, ignoreCase = true) ||
                    m.vendor.fullName.contains(query, ignoreCase = true) ||
                    m.location.address.contains(query, ignoreCase = true)) &&
                    (type == null || m.type == type) &&
                    (status == null || m.status == status) &&
                    (sectorId == null || m.location.sectorId == sectorId)
            }
            .map { m ->
                MapMarker(
                    id = m.id.toString(),
                    position = m.location.toLatLng(),
                    title = m.name,
                    color = m.type.markerColor(),
                )
            }

    private fun construirPoligonos(sectors: List<Sector>): List<MapPolygon> =
        sectors.mapNotNull { sector ->
            val points = SECTOR_POLYGONS[sector.id] ?: return@mapNotNull null
            MapPolygon(
                id = "sector-${sector.id}",
                points = points,
                fillColor = sector.saturationLevel.fillColor(),
                strokeColor = sector.saturationLevel.strokeColor(),
                strokeWidthPx = 3f,
            )
        }
}

/** Color del marcador según tipo de módulo. */
private fun ModuleType.markerColor(): Color = when (this) {
    ModuleType.FORMAL   -> Green900
    ModuleType.INFORMAL -> Amber500
    ModuleType.FERIA    -> ColorFeria
}
