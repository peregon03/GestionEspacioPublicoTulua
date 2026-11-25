package co.gov.tulua.espaciopublico.ui.map

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.gov.tulua.espaciopublico.R
import co.gov.tulua.espaciopublico.core.designsystem.TuluaTheme
import co.gov.tulua.espaciopublico.domain.model.ModuleStatus
import co.gov.tulua.espaciopublico.domain.model.Sector

/**
 * Bottom sheet de filtros avanzados del mapa.
 *
 * Permite filtrar por sector y por estado del módulo. Las selecciones son
 * locales al sheet hasta que el usuario toca "Aplicar filtros".
 *
 * @param sectors          Lista de sectores disponibles.
 * @param currentSectorId  Sector actualmente activo (null = todos).
 * @param currentStatus    Estado actualmente activo (null = todos).
 * @param sheetState       Estado del sheet.
 * @param onDismiss        Callback al cerrar sin aplicar.
 * @param onApply          Callback al tocar "Aplicar filtros". Recibe (sectorId, status).
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MapFiltersSheet(
    sectors: List<Sector>,
    currentSectorId: Long?,
    currentStatus: ModuleStatus?,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onApply: (sectorId: Long?, status: ModuleStatus?) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Estado local: se confirma solo al presionar "Aplicar".
    var selectedSectorId by remember(currentSectorId) { mutableStateOf(currentSectorId) }
    var selectedStatus by remember(currentStatus) { mutableStateOf(currentStatus) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // ── Título ─────────────────────────────────────────────────────────
            Text(
                text = stringResource(R.string.map_filter_advanced),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface,
            )

            // ── Selector de sector ─────────────────────────────────────────────
            Text(
                text = stringResource(R.string.map_filter_sector),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                // Chip "Todos los sectores"
                FilterChip(
                    selected = selectedSectorId == null,
                    onClick = { selectedSectorId = null },
                    label = {
                        Text(
                            text = stringResource(R.string.map_filter_all_sectors),
                            style = MaterialTheme.typography.labelLarge,
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
                )
                sectors.forEach { sector ->
                    FilterChip(
                        selected = selectedSectorId == sector.id,
                        onClick = { selectedSectorId = if (selectedSectorId == sector.id) null else sector.id },
                        label = {
                            Text(
                                text = sector.name,
                                style = MaterialTheme.typography.labelLarge,
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        ),
                    )
                }
            }

            // ── Selector de estado ─────────────────────────────────────────────
            Text(
                text = stringResource(R.string.map_filter_status),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                // Chip "Todos los estados"
                FilterChip(
                    selected = selectedStatus == null,
                    onClick = { selectedStatus = null },
                    label = {
                        Text(
                            text = stringResource(R.string.map_filter_all_statuses),
                            style = MaterialTheme.typography.labelLarge,
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
                )
                ModuleStatus.entries.forEach { status ->
                    FilterChip(
                        selected = selectedStatus == status,
                        onClick = { selectedStatus = if (selectedStatus == status) null else status },
                        label = {
                            Text(
                                text = status.etiquetaFiltro(),
                                style = MaterialTheme.typography.labelLarge,
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        ),
                    )
                }
            }

            Spacer(Modifier.height(4.dp))

            // ── Botón aplicar ──────────────────────────────────────────────────
            Button(
                onClick = { onApply(selectedSectorId, selectedStatus) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = stringResource(R.string.map_filter_apply),
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}

/** Etiqueta de estado para los chips de filtro. */
private fun ModuleStatus.etiquetaFiltro(): String = when (this) {
    ModuleStatus.APROBADO    -> "Aprobado"
    ModuleStatus.EN_REVISION -> "En revision"
    ModuleStatus.RECHAZADO   -> "Rechazado"
    ModuleStatus.SUSPENDIDO  -> "Suspendido"
}

// ─── Preview ──────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun MapFiltersSheetPreview() {
    TuluaTheme {
        MapFiltersSheet(
            sectors = listOf(
                co.gov.tulua.espaciopublico.domain.model.Sector(
                    id = 1L,
                    name = "Carrera 25",
                    moduleCount = 48,
                    occupancyPercent = 85f,
                    saturationLevel = co.gov.tulua.espaciopublico.domain.model.SaturationLevel.ALTA,
                ),
                co.gov.tulua.espaciopublico.domain.model.Sector(
                    id = 2L,
                    name = "Galería y Plaza de Mercado",
                    moduleCount = 62,
                    occupancyPercent = 92f,
                    saturationLevel = co.gov.tulua.espaciopublico.domain.model.SaturationLevel.CRITICA,
                ),
            ),
            currentSectorId = null,
            currentStatus = null,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            onDismiss = {},
            onApply = { _, _ -> },
        )
    }
}
