package co.gov.tulua.espaciopublico.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import co.gov.tulua.espaciopublico.core.designsystem.Amber500
import co.gov.tulua.espaciopublico.core.designsystem.Green900
import co.gov.tulua.espaciopublico.core.designsystem.Gray200
import co.gov.tulua.espaciopublico.core.designsystem.Red700
import co.gov.tulua.espaciopublico.core.designsystem.TuluaTheme

data class Segment(
    val value: Float,
    val color: Color,
    val label: String? = null,
)

@Composable
fun ProgressBarSegmented(
    segments: List<Segment>,
    modifier: Modifier = Modifier,
    height: Dp = 8.dp,
    trackColor: Color = Gray200,
) {
    val total = segments.sumOf { it.value.toDouble() }.toFloat().coerceAtLeast(1f)
    val shape = RoundedCornerShape(height / 2)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(shape)
            .background(trackColor),
    ) {
        Row(modifier = Modifier.matchParentSize()) {
            segments.forEach { segment ->
                val fraction = (segment.value / total).coerceIn(0f, 1f)
                Box(
                    modifier = Modifier
                        .weight(fraction.coerceAtLeast(0.001f))
                        .fillMaxHeight()
                        .background(segment.color),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProgressBarSegmentedPreview() {
    TuluaTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("Formales vs Informales", style = MaterialTheme.typography.labelSmall)
            ProgressBarSegmented(
                segments = listOf(
                    Segment(24f, Green900, "Formales"),
                    Segment(16f, Amber500, "Informales"),
                ),
            )
            Text("Ocupación por nivel", style = MaterialTheme.typography.labelSmall)
            ProgressBarSegmented(
                segments = listOf(
                    Segment(60f, Green900),
                    Segment(25f, Amber500),
                    Segment(15f, Red700),
                ),
                height = 12.dp,
            )
        }
    }
}
