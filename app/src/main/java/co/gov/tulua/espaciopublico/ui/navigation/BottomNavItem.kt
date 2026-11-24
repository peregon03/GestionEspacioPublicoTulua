package co.gov.tulua.espaciopublico.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.People
import androidx.compose.ui.graphics.vector.ImageVector
import co.gov.tulua.espaciopublico.R

/**
 * Represents a single bottom-navigation item.
 *
 * @param destination   Type-safe navigation destination.
 * @param labelRes      String resource id for the tab label (max 8 chars).
 * @param selectedIcon  Icon shown when the tab is active.
 * @param unselectedIcon Icon shown when the tab is inactive.
 */
data class BottomNavItem(
    val destination: AppDestination,
    @StringRes val labelRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
)

/** Ordered list of bottom-navigation items matching the 5-tab design. */
val bottomNavItems = listOf(
    BottomNavItem(
        destination = AppDestination.Dashboard,
        labelRes = R.string.tab_inicio,
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
    ),
    BottomNavItem(
        destination = AppDestination.Map,
        labelRes = R.string.tab_mapa,
        selectedIcon = Icons.Filled.Map,
        unselectedIcon = Icons.Outlined.Map,
    ),
    BottomNavItem(
        destination = AppDestination.Census,
        labelRes = R.string.tab_censo,
        selectedIcon = Icons.Filled.People,
        unselectedIcon = Icons.Outlined.People,
    ),
    BottomNavItem(
        destination = AppDestination.Events,
        labelRes = R.string.tab_eventos,
        selectedIcon = Icons.Filled.CalendarMonth,
        unselectedIcon = Icons.Outlined.CalendarMonth,
    ),
    BottomNavItem(
        destination = AppDestination.Requests,
        labelRes = R.string.tab_tramites,
        selectedIcon = Icons.Filled.AccountBalance,
        unselectedIcon = Icons.Outlined.AccountBalance,
    ),
)
