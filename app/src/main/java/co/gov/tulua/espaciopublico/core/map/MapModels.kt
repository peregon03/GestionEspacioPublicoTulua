package co.gov.tulua.espaciopublico.core.map

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import co.gov.tulua.espaciopublico.domain.model.GeoLocation

/** Coordenada simple (lat/lon) independiente de osmdroid, para usar desde la UI y los ViewModels. */
@Immutable
data class LatLng(
    val latitude: Double,
    val longitude: Double,
)

fun GeoLocation.toLatLng(): LatLng = LatLng(latitude, longitude)

/**
 * Pin en el mapa.
 *
 * @param id identificador para saber que pin se toco (ej. id del modulo).
 * @param color tinte del pin; `null` usa el icono por defecto de osmdroid.
 */
@Immutable
data class MapMarker(
    val id: String,
    val position: LatLng,
    val title: String? = null,
    val color: Color? = null,
)

/** Zona/sector dibujado como poligono semitransparente. */
@Immutable
data class MapPolygon(
    val id: String,
    val points: List<LatLng>,
    val fillColor: Color,
    val strokeColor: Color = fillColor.copy(alpha = 1f),
    val strokeWidthPx: Float = 4f,
)

object TuluaMapDefaults {
    /** Centro historico de Tulua (Parque Boyaca). */
    val Center = LatLng(latitude = 4.0847, longitude = -76.1954)
    const val DefaultZoom = 16.0
    const val MinZoom = 12.0
    const val MaxZoom = 19.0
}
