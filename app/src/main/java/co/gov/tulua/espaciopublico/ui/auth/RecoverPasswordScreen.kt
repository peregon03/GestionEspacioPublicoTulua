package co.gov.tulua.espaciopublico.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.gov.tulua.espaciopublico.R
import co.gov.tulua.espaciopublico.core.designsystem.TuluaTheme
import co.gov.tulua.espaciopublico.core.designsystem.components.TuluaTopBar

/**
 * Recover password screen placeholder.
 * Full implementation is done in a later phase.
 */
@Composable
fun RecoverPasswordScreen(
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var email by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TuluaTopBar(
                title = stringResource(R.string.recover_password_title),
                onBack = onBack,
            )
        },
        modifier = modifier,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = stringResource(R.string.recover_password_description),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(32.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(stringResource(R.string.login_email)) },
                singleLine = true,
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = { /* Send recovery email — implemented later */ },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.small,
            ) {
                Text(stringResource(R.string.recover_password_button))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RecoverPasswordScreenPreview() {
    TuluaTheme {
        RecoverPasswordScreen()
    }
}
