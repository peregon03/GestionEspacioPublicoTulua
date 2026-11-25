package co.gov.tulua.espaciopublico.core.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.gov.tulua.espaciopublico.core.designsystem.Amber500
import co.gov.tulua.espaciopublico.core.designsystem.AmberContainer
import co.gov.tulua.espaciopublico.core.designsystem.CardBorder
import co.gov.tulua.espaciopublico.core.designsystem.Red700
import co.gov.tulua.espaciopublico.core.designsystem.RedContainer
import co.gov.tulua.espaciopublico.core.designsystem.TuluaTheme

@Composable
fun RequestCard(
    code: String,
    title: String,
    applicant: String,
    statusBadge: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    urgencyBadge: @Composable (() -> Unit)? = null,
    onClick: () -> Unit = {},
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, CardBorder),
        elevation = CardDefaults.cardElevation(0.dp),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = code,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    urgencyBadge?.invoke()
                    statusBadge()
                }
            }
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = applicant,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
fun UrgencyBadge(daysLeft: Int) {
    val (bg, text, label) = when {
        daysLeft <= 2 -> Triple(RedContainer, Red700, "Vence en ${daysLeft}d")
        daysLeft <= 5 -> Triple(AmberContainer, Amber500, "Vence en ${daysLeft}d")
        else -> return
    }
    Card(
        shape = MaterialTheme.shapes.extraSmall,
        colors = CardDefaults.cardColors(containerColor = bg),
        elevation = CardDefaults.cardElevation(0.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = text,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RequestCardPreview() {
    TuluaTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            RequestCard(
                code = "RAD-2024-0512",
                title = "Permiso para venta de alimentos en Carrera 25",
                applicant = "María Elena Gómez · 31.987.654",
                statusBadge = { StatusBadge(StatusType.WARNING, "Pendiente") },
                urgencyBadge = { UrgencyBadge(daysLeft = 2) },
            )
            RequestCard(
                code = "RAD-2024-0498",
                title = "Permiso puesto de frutas esquina",
                applicant = "Jorge Iván Castaño · 16.789.012",
                statusBadge = { StatusBadge(StatusType.ACTIVE, "Aprobada") },
            )
        }
    }
}
