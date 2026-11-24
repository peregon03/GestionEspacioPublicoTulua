package co.gov.tulua.espaciopublico.core.designsystem.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import co.gov.tulua.espaciopublico.R
import co.gov.tulua.espaciopublico.core.designsystem.TuluaTheme

/**
 * Reusable top app bar for all screens.
 *
 * @param title      Screen title string.
 * @param onBack     If non-null a back arrow button is shown; invoked on click.
 * @param actions    Optional trailing action icons (RowScope lambda).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TuluaTopBar(
    title: String,
    onBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
            )
        },
        navigationIcon = {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.cd_back),
                    )
                }
            }
        },
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
        ),
    )
}

// ─── Previews ─────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
private fun TuluaTopBarPreview() {
    TuluaTheme {
        TuluaTopBar(title = "Espacio Publico")
    }
}

@Preview(showBackground = true)
@Composable
private fun TuluaTopBarWithBackPreview() {
    TuluaTheme {
        TuluaTopBar(
            title = "Detalle",
            onBack = {},
        )
    }
}
