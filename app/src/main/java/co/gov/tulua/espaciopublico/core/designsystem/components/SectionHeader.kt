package co.gov.tulua.espaciopublico.core.designsystem.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.gov.tulua.espaciopublico.R
import co.gov.tulua.espaciopublico.core.designsystem.TuluaTheme

/**
 * Row containing a section title on the left and an optional "Ver todos" link on the right.
 *
 * @param title      Section heading text.
 * @param onVerTodos If non-null the "Ver todos" link is shown; invoked on click.
 * @param modifier   Modifier for the row.
 */
@Composable
fun SectionHeader(
    title: String,
    onVerTodos: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        if (onVerTodos != null) {
            Text(
                text = stringResource(R.string.ver_todos),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable(onClick = onVerTodos),
            )
        }
    }
}

// ─── Previews ─────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
private fun SectionHeaderPreview() {
    TuluaTheme {
        SectionHeader(
            title = "Vendedores cercanos",
            onVerTodos = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SectionHeaderNoActionPreview() {
    TuluaTheme {
        SectionHeader(
            title = "Estadisticas",
            modifier = Modifier.padding(16.dp),
        )
    }
}
