package co.gov.tulua.espaciopublico.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.gov.tulua.espaciopublico.core.designsystem.Amber500
import co.gov.tulua.espaciopublico.core.designsystem.CardBorder
import co.gov.tulua.espaciopublico.core.designsystem.Green900
import co.gov.tulua.espaciopublico.core.designsystem.Red700
import co.gov.tulua.espaciopublico.core.designsystem.TuluaTheme
import co.gov.tulua.espaciopublico.domain.model.SaturationLevel
import co.gov.tulua.espaciopublico.domain.model.Sector

// Rojo oscuro para nivel CRITICA
private val RedDark = Color(0xFF7B0000)

/**
 * Gráfico de barras horizontales personalizado mostrando módulos por sector.
 * Cada barra se colorea según el nivel de saturación del sector.
 *
 * @param sectors  Lista de sectores a mostrar.
 * @param modifier Modifier para el contenedor exterior.
 */
@Composable
fun SectorBarChart(
    sectors: List<Sector>,
    modifier: Modifier = Modifier,
) {
    if (sectors.isEmpty()) return

    val maxCount = sectors.maxOf { it.moduleCount }.coerceAtLeast(1)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        border = BorderStroke(1.dp, CardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            sectors.forEach { sector ->
                SectorBarRow(sector = sector, maxCount = maxCount)
            }
        }
    }
}

@Composable
private fun SectorBarRow(
    sector: Sector,
    maxCount: Int,
) {
    val barColor = sector.saturationLevel.toBarColor()
    val fraction = sector.moduleCount.toFloat() / maxCount.toFloat()

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // Etiqueta del sector: ancho fijo para alinear barras
        Text(
            text = sector.name.abreviado(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(0.35f),
        )

        // Barra
        Canvas(
            modifier = Modifier
                .weight(0.5f)
                .height(18.dp),
        ) {
            val barWidth = size.width * fraction
            val radius = size.height / 2f

            // Fondo gris claro
            drawRoundRect(
                color = Color.LightGray.copy(alpha = 0.3f),
                topLeft = Offset.Zero,
                size = Size(size.width, size.height),
                cornerRadius = CornerRadius(radius, radius),
            )

            // Barra coloreada
            if (barWidth > 0f) {
                drawRoundRect(
                    color = barColor,
                    topLeft = Offset.Zero,
                    size = Size(barWidth.coerceAtMost(size.width), size.height),
                    cornerRadius = CornerRadius(radius, radius),
                )
            }
        }

        // Conteo numérico
        Text(
            text = sector.moduleCount.toString(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(0.15f),
        )
    }
}

/** Convierte SaturationLevel al color de barra correspondiente. */
private fun SaturationLevel.toBarColor(): Color = when (this) {
    SaturationLevel.BAJA    -> Green900
    SaturationLevel.MEDIA   -> Amber500
    SaturationLevel.ALTA    -> Red700
    SaturationLevel.CRITICA -> RedDark
}

/**
 * Abrevia nombres de sector largos para que quepan en la etiqueta.
 * Ej: "Galería y Plaza de Mercado" → "Gal. y Plaza"
 */
private fun String.abreviado(): String {
    return if (length > 16) take(14).trimEnd() + "…" else this
}

// ─── Previews ─────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
private fun SectorBarChartPreview() {
    TuluaTheme {
        SectorBarChart(
            sectors = listOf(
                Sector(1L, "Carrera 25", moduleCount = 48, occupancyPercent = 85f, saturationLevel = SaturationLevel.ALTA),
                Sector(2L, "Galería y Plaza de Mercado", moduleCount = 62, occupancyPercent = 92f, saturationLevel = SaturationLevel.CRITICA),
                Sector(3L, "Parque Boyacá", moduleCount = 25, occupancyPercent = 60f, saturationLevel = SaturationLevel.MEDIA),
                Sector(4L, "Parque Céspedes", moduleCount = 18, occupancyPercent = 40f, saturationLevel = SaturationLevel.BAJA),
            ),
            modifier = Modifier.padding(16.dp),
        )
    }
}
