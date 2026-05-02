package com.gw.fitt.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.gw.fitt.presentation.coach.CoachScreen
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
                RoutineScreen()
            }
        }

        navigation(
            startDestination = Screen.Timer.route,
            route = Graph.TIMER
        ) {
            composable(Screen.Timer.route) {
                TimerScreen()
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

        navigation(
            startDestination = Screen.Coach.route,
            route = Graph.COACH
        ) {
            composable(Screen.Coach.route) {
                CoachScreen()
            }
        }
    }
}
