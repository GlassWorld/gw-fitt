package com.gw.fitt.presentation.home

import com.gw.fitt.domain.model.Routine
import com.gw.fitt.domain.model.WeeklyStats

data class HomeState(
    val isLoading: Boolean = true,
    val weeklyStats: WeeklyStats = WeeklyStats(),
    val recentRoutines: List<Routine> = emptyList(),
    val error: String? = null
)
