package com.gw.fitt.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.gw.fitt.ui.theme.fittColors

data class BottomNavItem(
    val label: String,
    val graphRoute: String,
    val icon: ImageVector
)

private val bottomNavItems = listOf(
    BottomNavItem(label = "홈",     graphRoute = Graph.HOME,    icon = Icons.Filled.Home),
    BottomNavItem(label = "루틴",   graphRoute = Graph.ROUTINE, icon = Icons.Filled.FitnessCenter),
    BottomNavItem(label = "타이머", graphRoute = Graph.TIMER,   icon = Icons.Filled.Timer),
    BottomNavItem(label = "기록",   graphRoute = Graph.LOG,     icon = Icons.Filled.BarChart),
    BottomNavItem(label = "AI코치", graphRoute = Graph.COACH,   icon = Icons.Filled.SmartToy),
)

@Composable
fun FittBottomNav(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val accent = MaterialTheme.fittColors.accent

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.background,
        tonalElevation = androidx.compose.ui.unit.Dp.Hairline
    ) {
        bottomNavItems.forEach { item ->
            val selected = currentDestination?.hierarchy?.any { it.route == item.graphRoute } == true

            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(item.graphRoute) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    selectedTextColor = MaterialTheme.colorScheme.onBackground,
                    indicatorColor = accent,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}
