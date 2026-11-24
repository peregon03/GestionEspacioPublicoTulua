package co.gov.tulua.espaciopublico.core.designsystem.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import co.gov.tulua.espaciopublico.core.designsystem.TuluaTheme

/**
 * Animated shimmer modifier. Apply to any Box/composable to give it a skeleton appearance.
 */
fun Modifier.shimmerEffect(): Modifier = composed {
    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.6f),
        Color.LightGray.copy(alpha = 0.2f),
        Color.LightGray.copy(alpha = 0.6f),
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnimation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmer_translate",
    )

    background(
        brush = Brush.linearGradient(
            colors = shimmerColors,
            start = Offset(x = translateAnimation - 200f, y = 0f),
            end = Offset(x = translateAnimation, y = 0f),
        ),
    )
}

/**
 * Convenience skeleton box with rounded corners.
 *
 * @param width    Width of the skeleton element (null = fillMaxWidth).
 * @param height   Height of the skeleton element.
 * @param modifier Additional modifier.
 */
@Composable
fun SkeletonBox(
    height: Dp,
    modifier: Modifier = Modifier,
    width: Dp? = null,
) {
    val base = if (width != null) modifier.width(width) else modifier.fillMaxWidth()
    Box(
        modifier = base
            .height(height)
            .background(
                color = Color.LightGray.copy(alpha = 0.3f),
                shape = MaterialTheme.shapes.small,
            )
            .shimmerEffect(),
    )
}

// ─── Previews ─────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
private fun SkeletonLoaderPreview() {
    TuluaTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Simulated card skeleton
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SkeletonBox(height = 48.dp, width = 48.dp)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
                    SkeletonBox(height = 14.dp)
                    SkeletonBox(height = 12.dp, modifier = Modifier.fillMaxWidth(0.6f))
                }
            }
            SkeletonBox(height = 100.dp)
            SkeletonBox(height = 100.dp)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SkeletonBox(height = 72.dp, modifier = Modifier.weight(1f))
                SkeletonBox(height = 72.dp, modifier = Modifier.weight(1f))
                SkeletonBox(height = 72.dp, modifier = Modifier.weight(1f))
            }
        }
    }
}
