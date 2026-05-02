package com.gw.fitt.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Routine : Screen("routine")
    object Timer : Screen("timer")
    object Log : Screen("log")
    object Coach : Screen("coach")
}

// 각 탭의 중첩 NavGraph 루트 경로
object Graph {
    const val ROOT = "root_graph"
    const val HOME = "home_graph"
    const val ROUTINE = "routine_graph"
    const val TIMER = "timer_graph"
    const val LOG = "log_graph"
    const val COACH = "coach_graph"
}
