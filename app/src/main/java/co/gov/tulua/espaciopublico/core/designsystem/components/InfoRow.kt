package co.gov.tulua.espaciopublico.core.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.gov.tulua.espaciopublico.core.designsystem.TuluaTheme

/**
 * Fila de etiqueta–valor para secciones de detalle.
 *
 * @param label    Texto de la etiqueta (columna izquierda, 40% del ancho).
 * @param value    Valor a mostrar (columna derecha).
 * @param modifier Modifier externo.
 */
@Composable
fun InfoRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.42f),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(0.58f),
            textAlign = TextAlign.End,
        )
    }
}

// ─── Previews ─────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
private fun InfoRowPreview() {
    TuluaTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            InfoRow(label = "Nombre", value = "Carlos Andres Perez")
            HorizontalDivider()
            InfoRow(label = "Cedula", value = "1234567890")
            HorizontalDivider()
            InfoRow(label = "Telefono", value = "318 456 7890")
            HorizontalDivider()
            InfoRow(label = "Correo", value = "carlos@ejemplo.com")
        }
    }
}
