package co.gov.tulua.espaciopublico.ui.map

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.gov.tulua.espaciopublico.R
import co.gov.tulua.espaciopublico.core.designsystem.TuluaTheme
import co.gov.tulua.espaciopublico.core.designsystem.components.FilterChipRow
import co.gov.tulua.espaciopublico.core.designsystem.components.SearchField
import co.gov.tulua.espaciopublico.core.designsystem.components.TuluaTopBar
import co.gov.tulua.espaciopublico.core.map.OsmMapView
import co.gov.tulua.espaciopublico.domain.model.ModuleType
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import org.osmdroid.views.MapView

/**
 * Referencia mutable al MapView activo. Permite que los botones de zoom
 * y ubicación interactúen con el controlador del mapa sin necesidad de
 * recomposición — el configure{} lambda la inicializa una sola vez.
 */
private class MapViewHolder {
    var mapView: MapView? = null
}

/**
 * Pantalla principal del mapa de módulos ambulantes.
 *
 * Integra:
 * - OsmMapView con pines por tipo y polígonos de sectores
 * - Barra de búsqueda y chips de filtro rápido superpuestos al mapa
 * - Controles de zoom y botón "Mi ubicación" flotantes
 * - Bottom sheet de detalle al tocar un pin
 * - Bottom sheet de filtros avanzados
 *
 * @param onNavigateToDetail Callback de navegación al detalle del módulo.
 * @param viewModel          ViewModel inyectado por Hilt.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    onNavigateToDetail: (moduleId: String) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: MapViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Referencia al MapView para controlar zoom y ubicación desde los FABs
    val mapHolder = remember { MapViewHolder() }

    // ── Sheets ────────────────────────────────────────────────────────────────
    val detailSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    var showFiltersSheet by rememberSaveable { mutableStateOf(false) }
    val filtersSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Launcher para el permiso de ubicación (ya declarado en AndroidManifest)
    val locationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) {
            centrarEnMiUbicacion(context, mapHolder)
        } else {
            Toast.makeText(
                context,
                context.getString(R.string.map_location_denied),
                Toast.LENGTH_SHORT,
            ).show()
        }
    }

    // Número de filtros avanzados activos (badge en el ícono de embudo)
    val activeFiltersCount = listOfNotNull(
        uiState.filterStatus,
        uiState.filterSectorId,
    ).size

    // Opciones de chips de tipo (índice 0 = Todos)
    val typeChipLabels = listOf(
        stringResource(R.string.map_all),
        stringResource(R.string.map_formal),
        stringResource(R.string.map_informal),
        stringResource(R.string.map_feria),
    )
    val selectedTypeIndex = when (uiState.filterType) {
        null                -> 0
        ModuleType.FORMAL   -> 1
        ModuleType.INFORMAL -> 2
        ModuleType.FERIA    -> 3
    }

    Scaffold(
        topBar = { TuluaTopBar(title = stringResource(R.string.screen_mapa)) },
        modifier = modifier,
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            // ── Mapa de fondo ─────────────────────────────────────────────────
            OsmMapView(
                modifier = Modifier.fillMaxSize(),
                markers = uiState.markers,
                polygons = uiState.polygons,
                onMarkerClick = { marker ->
                    viewModel.selectModule(marker.id)
                    scope.launch { detailSheetState.show() }
                },
                onMapClick = {
                    // Toque en zona vacía → cerrar sheet de detalle si está abierto
                    if (uiState.selectedModule != null) {
                        viewModel.clearSelection()
                        scope.launch { detailSheetState.hide() }
                    }
                },
                configure = { mv ->
                    // Guarda la referencia para controlar zoom y ubicación desde los FABs
                    mapHolder.mapView = mv
                },
            )

            // ── Overlay superior: búsqueda + chips + botón filtros ────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    SearchField(
                        query = uiState.searchQuery,
                        onQueryChange = viewModel::updateSearch,
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                                shape = MaterialTheme.shapes.small,
                            ),
                    )

                    Spacer(Modifier.size(8.dp))

                    // Botón embudo con badge de filtros activos
                    BadgedBox(
                        badge = {
                            if (activeFiltersCount > 0) {
                                Badge { Text(activeFiltersCount.toString()) }
                            }
                        },
                    ) {
                        FloatingActionButton(
                            onClick = { showFiltersSheet = true },
                            modifier = Modifier.size(48.dp),
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = if (activeFiltersCount > 0)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant,
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = stringResource(R.string.cd_map_advanced_filters),
                            )
                        }
                    }
                }

                Spacer(Modifier.height(6.dp))

                // Chips de tipo rápido
                FilterChipRow(
                    options = typeChipLabels,
                    selectedIndex = selectedTypeIndex,
                    onSelect = { idx ->
                        val newType = when (idx) {
                            1    -> ModuleType.FORMAL
                            2    -> ModuleType.INFORMAL
                            3    -> ModuleType.FERIA
                            else -> null
                        }
                        viewModel.updateFilterType(newType)
                    },
                    modifier = Modifier.padding(vertical = 2.dp),
                )
            }

            // ── Controles flotantes: zoom + mi ubicación ──────────────────────
            Column(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                SmallFloatingActionButton(
                    onClick = {
                        mapHolder.mapView?.controller?.zoomIn()
                    },
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.cd_map_zoom_in),
                    )
                }

                SmallFloatingActionButton(
                    onClick = {
                        mapHolder.mapView?.controller?.zoomOut()
                    },
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = stringResource(R.string.cd_map_zoom_out),
                    )
                }

                SmallFloatingActionButton(
                    onClick = {
                        locationLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
                    },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.primary,
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = stringResource(R.string.cd_map_my_location),
                    )
                }
            }

            // ── Mensaje de error flotante ──────────────────────────────────────
            if (uiState.error != null) {
                Text(
                    text = uiState.error!!,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onError,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                        .background(
                            color = MaterialTheme.colorScheme.error,
                            shape = MaterialTheme.shapes.small,
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                )
            }
        }

        // ── Bottom sheet de detalle del módulo ────────────────────────────────
        val selectedModule = uiState.selectedModule
        if (selectedModule != null) {
            ModuleDetailSheet(
                module = selectedModule,
                sheetState = detailSheetState,
                onDismiss = {
                    viewModel.clearSelection()
                    scope.launch { detailSheetState.hide() }
                },
                onVerDetalle = { moduleId ->
                    viewModel.clearSelection()
                    scope.launch { detailSheetState.hide() }
                    onNavigateToDetail(moduleId)
                },
                onReportar = {
                    Toast.makeText(
                        context,
                        context.getString(R.string.map_report_sent),
                        Toast.LENGTH_SHORT,
                    ).show()
                },
            )
        }

        // ── Bottom sheet de filtros avanzados ─────────────────────────────────
        if (showFiltersSheet) {
            MapFiltersSheet(
                sectors = uiState.sectors,
                currentSectorId = uiState.filterSectorId,
                currentStatus = uiState.filterStatus,
                sheetState = filtersSheetState,
                onDismiss = {
                    showFiltersSheet = false
                    scope.launch { filtersSheetState.hide() }
                },
                onApply = { sectorId, status ->
                    viewModel.updateFilterSector(sectorId)
                    viewModel.updateFilterStatus(status)
                    showFiltersSheet = false
                    scope.launch { filtersSheetState.hide() }
                },
            )
        }
    }
}

/**
 * Obtiene la última ubicación conocida y centra el mapa en ella.
 * Solo se invoca después de que el permiso ACCESS_FINE_LOCATION fue concedido.
 */
private fun centrarEnMiUbicacion(
    context: android.content.Context,
    mapHolder: MapViewHolder,
) {
    try {
        val fusedClient = LocationServices.getFusedLocationProviderClient(context)
        @Suppress("MissingPermission")
        fusedClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val geoPoint = org.osmdroid.util.GeoPoint(location.latitude, location.longitude)
                mapHolder.mapView?.controller?.animateTo(geoPoint)
            }
        }
    } catch (e: SecurityException) {
        // No debería ocurrir: el permiso fue verificado antes de llamar esta función.
    }
}

// ─── Preview ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
private fun MapScreenPreview() {
    TuluaTheme {
        MapScreenPlaceholder()
    }
}

/** Versión sin ViewModel para Preview — muestra el placeholder del mapa. */
@Composable
private fun MapScreenPlaceholder(modifier: Modifier = Modifier) {
    Scaffold(
        topBar = { TuluaTopBar(title = "Mapa de Vendedores") },
        modifier = modifier,
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            OsmMapView(modifier = Modifier.fillMaxSize())
        }
    }
}
