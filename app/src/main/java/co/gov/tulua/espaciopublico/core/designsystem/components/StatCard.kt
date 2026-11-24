package co.gov.tulua.espaciopublico.core.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.gov.tulua.espaciopublico.core.designsystem.Amber500
import co.gov.tulua.espaciopublico.core.designsystem.AmberContainer
import co.gov.tulua.espaciopublico.core.designsystem.CardBorder
import co.gov.tulua.espaciopublico.core.designsystem.Green900
import co.gov.tulua.espaciopublico.core.designsystem.GreenContainer
import co.gov.tulua.espaciopublico.core.designsystem.Red700
import co.gov.tulua.espaciopublico.core.designsystem.RedContainer
import co.gov.tulua.espaciopublico.core.designsystem.TuluaTheme

/**
 * Compact stat card showing a label, a value, and an optional delta badge.
 * Supports up to 3 data points via composition (use multiple cards in a row).
 *
 * @param label     Short descriptive label (Caption 11sp).
 * @param value     Numeric or short text value (Title 18sp bold).
 * @param delta     Optional delta string e.g. "+12%" — colored automatically.
 * @param modifier  Modifier for sizing/layout.
 */
@Composable
fun StatCard(
    label: String,
    value: String,
    delta: String? = null,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        border = BorderStroke(1.dp, CardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                if (delta != null) {
                    Spacer(Modifier.width(6.dp))
                    DeltaBadge(delta = delta)
                }
            }
        }
    }
}

@Composable
private fun DeltaBadge(delta: String) {
    val isPositive = delta.startsWith("+")
    val isNegative = delta.startsWith("-")
    val (background, textColor) = when {
        isPositive -> GreenContainer to Green900
        isNegative -> RedContainer to Red700
        else       -> AmberContainer to Amber500
    }
    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .height(20.dp)
            .padding(horizontal = 4.dp),
        contentAlignment = Alignment.Center,
    ) {
        Card(
            shape = MaterialTheme.shapes.extraSmall,
            colors = CardDefaults.cardColors(containerColor = background),
            elevation = CardDefaults.cardElevation(0.dp),
        ) {
            Text(
                text = delta,
                style = MaterialTheme.typography.labelSmall,
                color = textColor,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            )
        }
    }
}

// ─── Previews ─────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
private fun StatCardPreview() {
    TuluaTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            StatCard(label = "Vendedores", value = "1,240", delta = "+5%", modifier = Modifier.weight(1f))
            StatCard(label = "Denuncias", value = "87", delta = "-3%", modifier = Modifier.weight(1f))
            StatCard(label = "Tramites", value = "34", modifier = Modifier.weight(1f))
        }
    }
}
