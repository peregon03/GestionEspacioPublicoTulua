package co.gov.tulua.espaciopublico.core.designsystem.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.gov.tulua.espaciopublico.core.designsystem.TuluaTheme

/**
 * Horizontally scrollable row of filter chips.
 *
 * @param options        List of chip labels.
 * @param selectedIndex  Index of the currently selected chip.
 * @param onSelect       Callback invoked with the new selected index.
 * @param modifier       Modifier for the outer row container.
 */
@Composable
fun FilterChipRow(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        options.forEachIndexed { index, label ->
            FilterChip(
                selected = index == selectedIndex,
                onClick = { onSelect(index) },
                label = {
                    Text(
                        text = label,
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
}

// ─── Previews ─────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
private fun FilterChipRowPreview() {
    TuluaTheme {
        var selected by remember { mutableIntStateOf(0) }
        FilterChipRow(
            options = listOf("Todos", "Activos", "Pendientes", "Vencidos", "Inactivos"),
            selectedIndex = selected,
            onSelect = { selected = it },
            modifier = Modifier.padding(16.dp),
        )
    }
}
