package co.gov.tulua.espaciopublico.ui.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.gov.tulua.espaciopublico.R
import co.gov.tulua.espaciopublico.core.designsystem.GreenContainer
import co.gov.tulua.espaciopublico.core.designsystem.TuluaTheme
import co.gov.tulua.espaciopublico.core.designsystem.components.InfoRow
import co.gov.tulua.espaciopublico.core.designsystem.components.StatusBadge
import co.gov.tulua.espaciopublico.core.designsystem.components.StatusType
import co.gov.tulua.espaciopublico.domain.model.GeoLocation
import co.gov.tulua.espaciopublico.domain.model.Module
import co.gov.tulua.espaciopublico.domain.model.ModuleStatus
import co.gov.tulua.espaciopublico.domain.model.ModuleType
import co.gov.tulua.espaciopublico.domain.model.Vendor

/**
 * Bottom sheet de detalle de un módulo del mapa.
 *
 * Muestra información resumida siempre visible (peek) y contenido expandido
 * con todos los datos del módulo.
 *
 * @param module       Módulo a mostrar.
 * @param sheetState   Estado del sheet (pasa desde MapScreen para controlar dismiss).
 * @param onDismiss    Callback al cerrar el sheet.
 * @param onVerDetalle Callback al tocar "Ver detalle".
 * @param onReportar   Callback al tocar "Reportar".
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModuleDetailSheet(
    module: Module,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onVerDetalle: (moduleId: String) -> Unit,
    onReportar: (moduleId: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            // ── Cabecera del sheet ──────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Miniatura / placeholder del módulo
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            color = GreenContainer,
                            shape = MaterialTheme.shapes.medium,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp),
                    )
                }

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = module.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = module.code,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Spacer(Modifier.width(8.dp))
                StatusBadge(
                    status = module.status.toStatusType(),
                    label = module.status.etiqueta(),
                )
            }

            Spacer(Modifier.height(12.dp))

            // Titular
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = module.vendor.fullName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }

            Spacer(Modifier.height(4.dp))

            // Dirección
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = module.location.address,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(Modifier.height(12.dp))

            // ── Información extendida ───────────────────────────────────────────
            // Tipo de módulo
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.map_tipo_badge),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                StatusBadge(
                    status = module.type.toStatusType(),
                    label = module.type.etiqueta(),
                )
            }

            Spacer(Modifier.height(4.dp))

            InfoRow(
                label = stringResource(R.string.map_area_label),
                value = "%.1f".format(module.areaM2),
            )
            HorizontalDivider()

            if (module.widthM != null && module.lengthM != null) {
                InfoRow(
                    label = stringResource(R.string.map_dimensions_label),
                    value = "%.1f m × %.1f m".format(module.widthM, module.lengthM),
                )
                HorizontalDivider()
            }

            if (module.validUntil != null) {
                InfoRow(
                    label = stringResource(R.string.map_valid_until_label),
                    value = module.validUntil,
                )
                HorizontalDivider()
            }

            Spacer(Modifier.height(20.dp))

            // ── Botones de acción ───────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedButton(
                    onClick = { onReportar(module.id.toString()) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error,
                    ),
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.map_reportar),
                        style = MaterialTheme.typography.labelLarge,
                    )
                }

                Button(
                    onClick = { onVerDetalle(module.id.toString()) },
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        text = stringResource(R.string.map_ver_detalle),
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
        }
    }
}

// ─── Extensiones internas para acceso desde este archivo ──────────────────────

private fun ModuleStatus.toStatusType(): StatusType = when (this) {
    ModuleStatus.APROBADO    -> StatusType.ACTIVE
    ModuleStatus.EN_REVISION -> StatusType.WARNING
    ModuleStatus.RECHAZADO   -> StatusType.DANGER
    ModuleStatus.SUSPENDIDO  -> StatusType.INACTIVE
}

private fun ModuleStatus.etiqueta(): String = when (this) {
    ModuleStatus.APROBADO    -> "Aprobado"
    ModuleStatus.EN_REVISION -> "En revision"
    ModuleStatus.RECHAZADO   -> "Rechazado"
    ModuleStatus.SUSPENDIDO  -> "Suspendido"
}

private fun ModuleType.toStatusType(): StatusType = when (this) {
    ModuleType.FORMAL   -> StatusType.ACTIVE
    ModuleType.INFORMAL -> StatusType.WARNING
    ModuleType.FERIA    -> StatusType.INACTIVE
}

private fun ModuleType.etiqueta(): String = when (this) {
    ModuleType.FORMAL   -> "Formal"
    ModuleType.INFORMAL -> "Informal"
    ModuleType.FERIA    -> "Feria"
}

// ─── Preview ──────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun ModuleDetailSheetPreview() {
    TuluaTheme {
        ModuleDetailSheet(
            module = previewModule(),
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false),
            onDismiss = {},
            onVerDetalle = {},
            onReportar = {},
        )
    }
}

private fun previewModule() = Module(
    id = 1L,
    code = "MOD-2025-0001",
    name = "Puesto de frutas La Esquina",
    type = ModuleType.FORMAL,
    category = "Alimentos",
    status = ModuleStatus.APROBADO,
    vendor = Vendor(id = 1L, fullName = "Carlos Andres Perez", idNumber = "1234567890"),
    location = GeoLocation(address = "Cra 25 #25-10", latitude = 4.0855, longitude = -76.1958, sectorId = 1L),
    areaM2 = 4.0,
    widthM = 2.0,
    lengthM = 2.0,
    validUntil = "2026-06-30",
)
