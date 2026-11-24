package co.gov.tulua.espaciopublico.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import co.gov.tulua.espaciopublico.core.designsystem.TuluaTheme
import co.gov.tulua.espaciopublico.ui.navigation.AppDestination
import co.gov.tulua.espaciopublico.ui.navigation.AppNavGraph
import co.gov.tulua.espaciopublico.ui.navigation.BottomNavItem
import co.gov.tulua.espaciopublico.ui.navigation.bottomNavItems
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TuluaTheme {
                TuluaApp()
            }
        }
    }
}

@Composable
private fun TuluaApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Destinations that should NOT show the bottom nav bar
    val hideBottomBarDestinations = setOf(
        AppDestination.Login::class,
        AppDestination.RecoverPassword::class,
    )

    val showBottomBar = hideBottomBarDestinations.none { route ->
        currentDestination?.hasRoute(route) == true
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                TuluaBottomNavBar(
                    items = bottomNavItems,
                    currentDestination = currentDestination,
                    onNavigate = { item ->
                        navController.navigate(item.destination) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
        },
    ) { innerPadding ->
        AppNavGraph(
            navController = navController,
            startDestination = AppDestination.Login,
            modifier = Modifier.padding(innerPadding),
        )
    }
}

@Composable
private fun TuluaBottomNavBar(
    items: List<BottomNavItem>,
    currentDestination: androidx.navigation.NavDestination?,
    onNavigate: (BottomNavItem) -> Unit,
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = androidx.compose.ui.unit.dp * 2,
    ) {
        items.forEach { item ->
            val selected = currentDestination?.hasRoute(item.destination::class) == true
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(item) },
                icon = {
                    Icon(
                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = stringResource(item.labelRes),
                    )
                },
                label = {
                    Text(
                        text = stringResource(item.labelRes),
                        fontSize = 11.sp,
                        maxLines = 1,
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            )
        }
    }
}
