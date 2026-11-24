package co.gov.tulua.espaciopublico.core.designsystem.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.gov.tulua.espaciopublico.R
import co.gov.tulua.espaciopublico.core.designsystem.TuluaTheme

@Composable
fun ConfirmDialog(
    title: String,
    message: String,
    confirmText: String = stringResource(R.string.action_confirm),
    dismissText: String = stringResource(R.string.action_cancel),
    showReasonField: Boolean = false,
    reasonLabel: String = "",
    minReasonLength: Int = 10,
    onConfirm: (reason: String?) -> Unit,
    onDismiss: () -> Unit,
) {
    var reason by remember { mutableStateOf("") }
    val canConfirm = !showReasonField || reason.length >= minReasonLength

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title, style = MaterialTheme.typography.titleMedium) },
        text = {
            Column {
                Text(text = message, style = MaterialTheme.typography.bodyMedium)
                if (showReasonField) {
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = reason,
                        onValueChange = { reason = it },
                        label = { Text(reasonLabel) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        maxLines = 4,
                        supportingText = {
                            if (reason.isNotEmpty() && reason.length < minReasonLength) {
                                Text("Mínimo $minReasonLength caracteres (${reason.length}/$minReasonLength)")
                            }
                        },
                        isError = reason.isNotEmpty() && reason.length < minReasonLength,
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(if (showReasonField) reason else null) },
                enabled = canConfirm,
            ) {
                Text(confirmText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(dismissText)
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun ConfirmDialogPreview() {
    TuluaTheme {
        ConfirmDialog(
            title = "Rechazar solicitud",
            message = "¿Está seguro de rechazar esta solicitud?",
            showReasonField = true,
            reasonLabel = "Motivo del rechazo",
            onConfirm = {},
            onDismiss = {},
        )
    }
}
