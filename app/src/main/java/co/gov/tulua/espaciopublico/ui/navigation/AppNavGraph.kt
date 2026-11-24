package co.gov.tulua.espaciopublico.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import co.gov.tulua.espaciopublico.ui.auth.LoginScreen
import co.gov.tulua.espaciopublico.ui.census.CensusScreen
import co.gov.tulua.espaciopublico.ui.dashboard.DashboardScreen
import co.gov.tulua.espaciopublico.ui.events.EventsScreen
import co.gov.tulua.espaciopublico.ui.map.MapScreen
import co.gov.tulua.espaciopublico.ui.requests.RequestsScreen

/**
 * Root navigation graph for the application.
 *
 * @param navController    NavHostController provided by the host Activity.
 * @param startDestination The initial destination shown on launch.
 * @param modifier         Modifier applied to the NavHost.
 */
@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: AppDestination = AppDestination.Login,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        // ─── Auth ──────────────────────────────────────────────────────────────
        composable<AppDestination.Login> {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(AppDestination.Dashboard) {
                        popUpTo(AppDestination.Login) { inclusive = true }
                    }
                },
            )
        }

        composable<AppDestination.RecoverPassword> {
            // Placeholder — implemented in a later phase
            co.gov.tulua.espaciopublico.ui.auth.RecoverPasswordScreen(
                onBack = { navController.popBackStack() },
            )
        }

        // ─── Main bottom-nav destinations ─────────────────────────────────────
        composable<AppDestination.Dashboard> {
            DashboardScreen()
        }

        composable<AppDestination.Map> {
            MapScreen()
        }

        composable<AppDestination.Census> {
            CensusScreen()
        }

        composable<AppDestination.Events> {
            EventsScreen()
        }

        composable<AppDestination.Requests> {
            RequestsScreen()
        }
    }
}
