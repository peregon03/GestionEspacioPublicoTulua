package co.gov.tulua.espaciopublico.core.map

import co.gov.tulua.espaciopublico.domain.model.GeoLocation
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MapModelsTest {

    @Test
    fun geoLocation_toLatLng_keepsCoordinates() {
        val location = GeoLocation(address = "Cra 25", latitude = 4.0851, longitude = -76.1960)

        val latLng = location.toLatLng()

        assertEquals(4.0851, latLng.latitude, 0.0)
        assertEquals(-76.1960, latLng.longitude, 0.0)
    }

    @Test
    fun latLng_roundTripThroughGeoPoint_isLossless() {
        val original = LatLng(4.0847, -76.1954)

        val result = original.toGeoPoint().toLatLng()

        assertEquals(original, result)
    }

    @Test
    fun defaults_centerIsInsideTulua_andZoomWithinBounds() {
        val center = TuluaMapDefaults.Center
        assertTrue(center.latitude in 4.0..4.2)
        assertTrue(center.longitude in -76.3..-76.1)
        assertTrue(TuluaMapDefaults.DefaultZoom in TuluaMapDefaults.MinZoom..TuluaMapDefaults.MaxZoom)
    }
}
