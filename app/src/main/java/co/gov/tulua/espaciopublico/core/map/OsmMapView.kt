package co.gov.tulua.espaciopublico.core.map

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import co.gov.tulua.espaciopublico.R
import co.gov.tulua.espaciopublico.core.designsystem.TuluaTheme
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.CopyrightOverlay
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon

/**
 * Mapa de OpenStreetMap (osmdroid) reutilizable en Compose. Gratis, sin API key.
 *
 * Uso:
 * ```
 * OsmMapView(
 *     markers = modules.map { MapMarker(it.id.toString(), it.location.toLatLng(), it.name, PinGreen) },
 *     polygons = sectors.map { ... },
 *     onMarkerClick = { viewModel.onModuleSelected(it.id) },
 * )
 * ```
 *
 * @param center centro inicial (solo se aplica al crear el mapa; el usuario puede moverse libremente).
 * @param zoom zoom inicial.
 * @param markers pines a dibujar. Al cambiar la lista se redibujan.
 * @param polygons zonas/sectores semitransparentes. Se dibujan debajo de los pines.
 * @param onMarkerClick toque sobre un pin.
 * @param onPolygonClick toque sobre una zona (fuera de un pin).
 * @param onMapClick toque sobre el mapa vacio (devuelve la coordenada, util para "seleccionar en mapa").
 * @param onMapLongClick toque largo sobre el mapa.
 * @param configure acceso directo al [MapView] para casos avanzados (clustering, overlays propios).
 */
@Composable
fun OsmMapView(
    modifier: Modifier = Modifier,
    center: LatLng = TuluaMapDefaults.Center,
    zoom: Double = TuluaMapDefaults.DefaultZoom,
    markers: List<MapMarker> = emptyList(),
    polygons: List<MapPolygon> = emptyList(),
    onMarkerClick: (MapMarker) -> Unit = {},
    onPolygonClick: (MapPolygon) -> Unit = {},
    onMapClick: (LatLng) -> Unit = {},
    onMapLongClick: (LatLng) -> Unit = {},
    configure: (MapView) -> Unit = {},
) {
    val description = stringResource(R.string.cd_map)
    val semanticsModifier = modifier.semantics { contentDescription = description }

    // En @Preview no hay red ni ciclo de vida real: se muestra un placeholder.
    if (LocalInspectionMode.current) {
        MapPreviewPlaceholder(semanticsModifier)
        return
    }

    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val currentOnMarkerClick by rememberUpdatedState(onMarkerClick)
    val currentOnPolygonClick by rememberUpdatedState(onPolygonClick)
    val currentOnMapClick by rememberUpdatedState(onMapClick)
    val currentOnMapLongClick by rememberUpdatedState(onMapLongClick)

    val mapView = remember {
        OsmConfig.init(context)
        createMapView(context, center, zoom).also(configure)
    }
    val drawn = remember { DrawnOverlays() }

    // Ciclo de vida: osmdroid necesita onResume/onPause y liberar recursos al salir.
    DisposableEffect(lifecycle, mapView) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                else -> Unit
            }
        }
        lifecycle.addObserver(observer)
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) mapView.onResume()
        onDispose {
            lifecycle.removeObserver(observer)
            mapView.onPause()
            mapView.onDetach()
        }
    }

    AndroidView(
        factory = { mapView },
        modifier = semanticsModifier,
        update = { view ->
            if (drawn.markers == markers && drawn.polygons == polygons && drawn.initialized) {
                return@AndroidView
            }
            view.overlays.clear()

            // 1) Eventos de toque en el mapa vacio (va primero = recibe el toque de ultimo).
            view.overlays.add(
                MapEventsOverlay(object : MapEventsReceiver {
                    override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                        currentOnMapClick(p.toLatLng())
                        return true
                    }

                    override fun longPressHelper(p: GeoPoint): Boolean {
                        currentOnMapLongClick(p.toLatLng())
                        return true
                    }
                }),
            )

            // 2) Poligonos (debajo de los pines).
            polygons.forEach { zone ->
                view.overlays.add(
                    Polygon(view).apply {
                        points = zone.points.map { it.toGeoPoint() }
                        fillPaint.color = zone.fillColor.toArgb()
                        outlinePaint.color = zone.strokeColor.toArgb()
                        outlinePaint.strokeWidth = zone.strokeWidthPx
                        setOnClickListener { _, _, _ ->
                            currentOnPolygonClick(zone)
                            true
                        }
                    },
                )
            }

            // 3) Pines.
            markers.forEach { pin ->
                view.overlays.add(
                    Marker(view).apply {
                        position = pin.position.toGeoPoint()
                        title = pin.title
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        pin.color?.let { tint ->
                            icon = icon?.mutate()?.let { d ->
                                DrawableCompat.wrap(d).also { DrawableCompat.setTint(it, tint.toArgb()) }
                            }
                        }
                        setOnMarkerClickListener { _, _ ->
                            currentOnMarkerClick(pin)
                            true
                        }
                    },
                )
            }

            // 4) Atribucion obligatoria de OpenStreetMap.
            view.overlays.add(CopyrightOverlay(view.context))

            drawn.markers = markers
            drawn.polygons = polygons
            drawn.initialized = true
            view.invalidate()
        },
    )
}

/** Guarda lo ultimo dibujado para no reconstruir overlays en cada recomposicion. */
private class DrawnOverlays {
    var initialized = false
    var markers: List<MapMarker> = emptyList()
    var polygons: List<MapPolygon> = emptyList()
}

private fun createMapView(context: Context, center: LatLng, zoom: Double): MapView =
    MapView(context).apply {
        setTileSource(TileSourceFactory.MAPNIK)
        setMultiTouchControls(true)
        isTilesScaledToDpi = true
        zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
        minZoomLevel = TuluaMapDefaults.MinZoom
        maxZoomLevel = TuluaMapDefaults.MaxZoom
        controller.setZoom(zoom)
        controller.setCenter(center.toGeoPoint())
    }

internal fun LatLng.toGeoPoint(): GeoPoint = GeoPoint(latitude, longitude)

internal fun GeoPoint.toLatLng(): LatLng = LatLng(latitude, longitude)

@Composable
private fun MapPreviewPlaceholder(modifier: Modifier) {
    Box(
        modifier = modifier.background(Color(0xFFE5E3DF)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.map_attribution),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OsmMapViewPreview() {
    TuluaTheme {
        OsmMapView(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp),
            markers = listOf(
                MapMarker(id = "1", position = TuluaMapDefaults.Center, title = "Modulo de prueba"),
            ),
        )
    }
}
