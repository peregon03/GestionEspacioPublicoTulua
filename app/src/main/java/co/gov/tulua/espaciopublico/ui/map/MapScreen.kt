package co.gov.tulua.espaciopublico.ui.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import co.gov.tulua.espaciopublico.R
import co.gov.tulua.espaciopublico.core.designsystem.Green900
import co.gov.tulua.espaciopublico.core.designsystem.TuluaTheme
import co.gov.tulua.espaciopublico.core.designsystem.components.TuluaTopBar
import co.gov.tulua.espaciopublico.core.map.LatLng
import co.gov.tulua.espaciopublico.core.map.MapMarker
import co.gov.tulua.espaciopublico.core.map.MapPolygon
import co.gov.tulua.espaciopublico.core.map.OsmMapView
import co.gov.tulua.espaciopublico.core.map.TuluaMapDefaults

// Datos de prueba del Sprint 1. En el Sprint 3 se reemplazan por los del ModuleRepository/SectorRepository.
private val samplePins = listOf(
    MapMarker(id = "demo-1", position = TuluaMapDefaults.Center, title = "Parque Boyaca", color = Green900),
)

private val sampleZones = listOf(
    MapPolygon(
        id = "demo-zone",
        points = listOf(
            LatLng(4.0862, -76.1972),
            LatLng(4.0862, -76.1936),
            LatLng(4.0832, -76.1936),
            LatLng(4.0832, -76.1972),
        ),
        fillColor = Color(0x332E7D32),
        strokeColor = Color(0xFF2E7D32),
    ),
)

@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            TuluaTopBar(title = stringResource(R.string.screen_mapa))
        },
        modifier = modifier,
    ) { innerPadding ->
        OsmMapView(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            markers = samplePins,
            polygons = sampleZones,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MapScreenPreview() {
    TuluaTheme {
        MapScreen()
    }
}
