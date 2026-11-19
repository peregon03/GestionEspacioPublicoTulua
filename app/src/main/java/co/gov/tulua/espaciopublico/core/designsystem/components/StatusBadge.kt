package co.gov.tulua.espaciopublico.core.designsystem.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.gov.tulua.espaciopublico.R
import co.gov.tulua.espaciopublico.core.designsystem.Amber500
import co.gov.tulua.espaciopublico.core.designsystem.AmberContainer
import co.gov.tulua.espaciopublico.core.designsystem.Gray400
import co.gov.tulua.espaciopublico.core.designsystem.Gray600
import co.gov.tulua.espaciopublico.core.designsystem.Green900
import co.gov.tulua.espaciopublico.core.designsystem.GreenContainer
import co.gov.tulua.espaciopublico.core.designsystem.Red700
import co.gov.tulua.espaciopublico.core.designsystem.RedContainer
import co.gov.tulua.espaciopublico.core.designsystem.TuluaTheme

/** Semantic status values used across the app. */
enum class StatusType {
    ACTIVE,      // green — activo / aprobado / en regla
    WARNING,     // amber — pendiente / por vencer
    DANGER,      // red   — infractor / vencido / rechazado
    INACTIVE,    // gray  — inactivo / suspendido
}

private data class BadgeColors(val background: Color, val text: Color)

private fun badgeColors(status: StatusType): BadgeColors = when (status) {
    StatusType.ACTIVE   -> BadgeColors(GreenContainer, Green900)
    StatusType.WARNING  -> BadgeColors(AmberContainer, Amber500)
    StatusType.DANGER   -> BadgeColors(RedContainer, Red700)
    StatusType.INACTIVE -> BadgeColors(Gray400, Gray600)
}

/**
 * Chip-style status badge.
 *
 * @param status   Semantic status determining color.
 * @param label    Short text displayed inside the badge.
 * @param modifier Modifier for positioning.
 */
@Composable
fun StatusBadge(
    status: StatusType,
    label: String,
    modifier: Modifier = Modifier,
) {
    val colors = badgeColors(status)
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.extraSmall,
        colors = CardDefaults.cardColors(containerColor = colors.background),
        elevation = CardDefaults.cardElevation(0.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = colors.text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        )
    }
}

// ─── Previews ─────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
private fun StatusBadgePreview() {
    TuluaTheme {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp),
        ) {
            StatusBadge(status = StatusType.ACTIVE, label = stringResource(R.string.status_active))
            StatusBadge(status = StatusType.WARNING, label = stringResource(R.string.status_warning))
            StatusBadge(status = StatusType.DANGER, label = stringResource(R.string.status_danger))
            StatusBadge(status = StatusType.INACTIVE, label = stringResource(R.string.status_inactive))
        }
    }
}
