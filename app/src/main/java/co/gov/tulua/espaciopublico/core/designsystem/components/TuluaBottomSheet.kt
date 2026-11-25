package co.gov.tulua.espaciopublico.core.designsystem.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.gov.tulua.espaciopublico.core.designsystem.TuluaTheme

/**
 * Bottom sheet con estilo Tulua.
 *
 * @param onDismiss   Invocado cuando el usuario cierra el sheet.
 * @param sheetState  Estado del sheet (opcional, usa el default si no se pasa).
 * @param content     Contenido del sheet en scope Column.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TuluaBottomSheet(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    content: @Composable ColumnScope.() -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier,
        content = content,
    )
}

// ─── Previews ─────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun TuluaBottomSheetPreview() {
    TuluaTheme {
        // El preview muestra solo el contenido ya que el ModalBottomSheet
        // necesita un host para renderizarse correctamente
        Text(
            text = "Contenido del bottom sheet",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(16.dp),
        )
    }
}
