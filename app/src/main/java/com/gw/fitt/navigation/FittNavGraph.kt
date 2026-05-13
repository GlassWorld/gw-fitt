package com.gw.fitt.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.gw.fitt.presentation.home.HomeScreen
import com.gw.fitt.presentation.log.LogScreen
import com.gw.fitt.presentation.routine.RoutineScreen
import com.gw.fitt.presentation.timer.TimerScreen

@Composable
fun FittNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Graph.HOME,
        route = Graph.ROOT,
        modifier = modifier
    ) {
        navigation(
            startDestination = Screen.Home.route,
            route = Graph.HOME
        ) {
            composable(Screen.Home.route) {
                HomeScreen()
            }
        }

        navigation(
            startDestination = Screen.Routine.route,
            route = Graph.ROUTINE
        ) {
            composable(Screen.Routine.route) {
                RoutineScreen(
                    onStartSelectedWorkout = { exercises ->
                        SelectedWorkoutHolder.set(exercises)
                        navController.navigate(Screen.Timer.selectedRoute)
                    }
                )
            }
        }

        navigation(
            startDestination = Screen.Timer.route,
            route = Graph.TIMER
        ) {
            composable(Screen.Timer.route) {
                TimerScreen()
            }
            composable(Screen.Timer.selectedRoute) {
                TimerScreen(
                    selectedExercises = SelectedWorkoutHolder.consume(),
                    onBackToSelection = {
                        navController.navigate(Graph.ROUTINE) {
                            popUpTo(Screen.Timer.selectedRoute) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(
                route = Screen.Timer.routineRoute,
                arguments = listOf(
                    navArgument("routineId") { type = NavType.IntType },
                    navArgument("routineName") { type = NavType.StringType },
                    navArgument("totalSets") { type = NavType.IntType }
                )
            ) { entry ->
                TimerScreen(
                    routineId = entry.arguments?.getInt("routineId") ?: 0,
                    routineName = entry.arguments?.getString("routineName").orEmpty(),
                    totalSets = entry.arguments?.getInt("totalSets") ?: 3
                )
            }
        }

        navigation(
            startDestination = Screen.Log.route,
            route = Graph.LOG
        ) {
            composable(Screen.Log.route) {
                LogScreen()
            }
        }

    }
}
