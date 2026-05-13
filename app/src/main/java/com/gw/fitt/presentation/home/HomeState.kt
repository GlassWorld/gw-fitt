package com.gw.fitt.presentation.home

import com.gw.fitt.domain.model.WeeklyStats
import com.gw.fitt.domain.model.WorkoutLog

data class HomeState(
    val isLoading: Boolean = true,
    val weeklyStats: WeeklyStats = WeeklyStats(),
    val recentWorkoutLogs: List<WorkoutLog> = emptyList(),
    val weightKg: Double? = null,
    val error: String? = null
)
