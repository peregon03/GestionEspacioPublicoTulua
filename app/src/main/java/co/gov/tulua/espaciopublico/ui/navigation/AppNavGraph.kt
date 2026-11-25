package co.gov.tulua.espaciopublico.ui.navigation

import android.content.Intent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import co.gov.tulua.espaciopublico.ui.auth.LoginScreen
import co.gov.tulua.espaciopublico.ui.auth.RecoverPasswordScreen
import co.gov.tulua.espaciopublico.ui.auth.SplashScreen
import co.gov.tulua.espaciopublico.ui.census.CensusScreen
import co.gov.tulua.espaciopublico.ui.census.CensusUiState
import co.gov.tulua.espaciopublico.ui.census.CensusViewModel
import co.gov.tulua.espaciopublico.ui.census.ModuleDetailScreen
import co.gov.tulua.espaciopublico.ui.census.exportModulesToCsv
import co.gov.tulua.espaciopublico.ui.census.wizard.NewCensusWizardScreen
import co.gov.tulua.espaciopublico.ui.dashboard.DashboardScreen
import co.gov.tulua.espaciopublico.ui.events.EventsScreen
import co.gov.tulua.espaciopublico.ui.map.MapScreen
import co.gov.tulua.espaciopublico.ui.requests.RequestsScreen

/**
 * Grafo de navegación raíz de la aplicación.
 *
 * @param navController    NavHostController provisto por la Activity anfitriona.
 * @param startDestination Destino inicial mostrado al arrancar.
 * @param modifier         Modifier aplicado al NavHost.
 */
@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: AppDestination = AppDestination.Splash,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = { fadeIn() },
        exitTransition = { fadeOut() },
        popEnterTransition = { slideInHorizontally { -it / 4 } + fadeIn() },
        popExitTransition = { slideOutHorizontally { it / 4 } + fadeOut() },
    ) {
        // ─── Splash ────────────────────────────────────────────────────────────
        composable<AppDestination.Splash>(
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() },
        ) {
            SplashScreen(
                onTimeout = {
                    navController.navigate(AppDestination.Login) {
                        popUpTo(AppDestination.Splash) { inclusive = true }
                    }
                },
            )
        }

        // ─── Auth ──────────────────────────────────────────────────────────────
        composable<AppDestination.Login>(
            enterTransition = { slideInHorizontally { -it } + fadeIn() },
            exitTransition = { slideOutHorizontally { -it } + fadeOut() },
        ) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(AppDestination.Dashboard) {
                        popUpTo(AppDestination.Login) { inclusive = true }
                    }
                },
                onNavigateToRecoverPassword = {
                    navController.navigate(AppDestination.RecoverPassword)
                },
            )
        }

        composable<AppDestination.RecoverPassword>(
            enterTransition = { slideInHorizontally { it } + fadeIn() },
            exitTransition = { slideOutHorizontally { it } + fadeOut() },
        ) {
            RecoverPasswordScreen(
                onBack = { navController.popBackStack() },
            )
        }

        // ─── Destinos principales del menú inferior ────────────────────────────
        composable<AppDestination.Dashboard> {
            DashboardScreen()
        }

        composable<AppDestination.Map> {
            MapScreen(
                onNavigateToDetail = { moduleId ->
                    moduleId.toLongOrNull()?.let { id ->
                        navController.navigate(AppDestination.ModuleDetail(id))
                    }
                },
            )
        }

        composable<AppDestination.Census> {
            val context = LocalContext.current
            val censusViewModel: CensusViewModel = hiltViewModel()
            val uiState by censusViewModel.uiState.collectAsStateWithLifecycle()

            CensusScreen(
                viewModel = censusViewModel,
                onModuleClick = { moduleId ->
                    navController.navigate(AppDestination.ModuleDetail(moduleId))
                },
                onNewCensus = {
                    navController.navigate(AppDestination.NewCensus)
                },
                onExportCsv = {
                    val modules = (uiState as? CensusUiState.Success)?.modules.orEmpty()
                    if (modules.isNotEmpty()) {
                        val uri = exportModulesToCsv(context, modules)
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/csv"
                            putExtra(Intent.EXTRA_STREAM, uri)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Exportar CSV"))
                    }
                },
            )
        }

        composable<AppDestination.Events> {
            EventsScreen()
        }

        composable<AppDestination.Requests> {
            RequestsScreen()
        }

        // ─── Destinos del censo ───────────────────────────────────────────────
        composable<AppDestination.ModuleDetail>(
            enterTransition = { slideInHorizontally { it } + fadeIn() },
            exitTransition = { slideOutHorizontally { it } + fadeOut() },
        ) {
            ModuleDetailScreen(
                onBack = { navController.popBackStack() },
            )
        }

        composable<AppDestination.NewCensus>(
            enterTransition = { slideInHorizontally { it } + fadeIn() },
            exitTransition = { slideOutHorizontally { it } + fadeOut() },
        ) {
            NewCensusWizardScreen(
                onCancel = { navController.popBackStack() },
                onSuccess = {
                    navController.navigate(AppDestination.Census) {
                        popUpTo(AppDestination.Census) { inclusive = true }
                    }
                },
            )
        }
    }
}
