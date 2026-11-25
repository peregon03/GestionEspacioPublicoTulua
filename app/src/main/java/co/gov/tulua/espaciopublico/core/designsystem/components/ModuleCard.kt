package co.gov.tulua.espaciopublico.core.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.gov.tulua.espaciopublico.core.designsystem.CardBorder
import co.gov.tulua.espaciopublico.core.designsystem.TuluaTheme
import co.gov.tulua.espaciopublico.domain.model.GeoLocation
import co.gov.tulua.espaciopublico.domain.model.Module
import co.gov.tulua.espaciopublico.domain.model.ModuleStatus
import co.gov.tulua.espaciopublico.domain.model.ModuleType
import co.gov.tulua.espaciopublico.domain.model.Vendor

/**
 * Tarjeta de módulo ambulante para listados del censo.
 *
 * @param module    Datos del módulo a mostrar.
 * @param onClick   Acción al tocar la tarjeta.
 * @param modifier  Modifier externo.
 */
@Composable
fun ModuleCard(
    module: Module,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        border = BorderStroke(1.dp, CardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            // Encabezado: código + badge de estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = module.code,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                StatusBadge(
                    status = module.status.toStatusType(),
                    label = module.status.etiqueta(),
                )
            }

            // Nombre del módulo
            Text(
                text = module.name,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            // Titular + tipo
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = module.vendor.fullName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.width(8.dp))
                StatusBadge(
                    status = module.type.toStatusType(),
                    label = module.type.etiqueta(),
                )
            }
        }
    }
}

// ─── Extensiones de mapeo ──────────────────────────────────────────────────────

internal fun ModuleStatus.toStatusType(): StatusType = when (this) {
    ModuleStatus.APROBADO    -> StatusType.ACTIVE
    ModuleStatus.EN_REVISION -> StatusType.WARNING
    ModuleStatus.RECHAZADO   -> StatusType.DANGER
    ModuleStatus.SUSPENDIDO  -> StatusType.INACTIVE
}

internal fun ModuleStatus.etiqueta(): String = when (this) {
    ModuleStatus.APROBADO    -> "Aprobado"
    ModuleStatus.EN_REVISION -> "En revision"
    ModuleStatus.RECHAZADO   -> "Rechazado"
    ModuleStatus.SUSPENDIDO  -> "Suspendido"
}

internal fun ModuleType.toStatusType(): StatusType = when (this) {
    ModuleType.FORMAL   -> StatusType.ACTIVE
    ModuleType.INFORMAL -> StatusType.WARNING
    ModuleType.FERIA    -> StatusType.INACTIVE
}

internal fun ModuleType.etiqueta(): String = when (this) {
    ModuleType.FORMAL   -> "Formal"
    ModuleType.INFORMAL -> "Informal"
    ModuleType.FERIA    -> "Feria"
}

// ─── Previews ─────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
private fun ModuleCardAprobadoPreview() {
    TuluaTheme {
        ModuleCard(
            module = previewModule(ModuleStatus.APROBADO, ModuleType.FORMAL),
            onClick = {},
            modifier = androidx.compose.ui.Modifier.padding(16.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ModuleCardRevisionPreview() {
    TuluaTheme {
        ModuleCard(
            module = previewModule(ModuleStatus.EN_REVISION, ModuleType.INFORMAL),
            onClick = {},
            modifier = androidx.compose.ui.Modifier.padding(16.dp),
        )
    }
}

private fun previewModule(status: ModuleStatus, type: ModuleType) = Module(
    id = 1L,
    code = "MOD-2025-0001",
    name = "Puesto de frutas La Esquina",
    type = type,
    category = "Alimentos",
    status = status,
    vendor = Vendor(id = 1L, fullName = "Carlos Andres Perez", idNumber = "1234567890"),
    location = GeoLocation(address = "Cra 25 #25-10", latitude = 4.0855, longitude = -76.1958, sectorId = 1L),
    areaM2 = 4.0,
    validUntil = "2026-06-30",
)
