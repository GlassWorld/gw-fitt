package com.gw.fitt.presentation.log

import com.gw.fitt.domain.model.WeeklyStats
import com.gw.fitt.domain.model.WorkoutLog

data class LogState(
    val isLoading: Boolean = true,
    val logs: List<WorkoutLog> = emptyList(),
    val weeklyStats: WeeklyStats = WeeklyStats(),
    val dailyMinutes: List<Float> = List(7) { 0f },  // 최근 7일 일별 운동 시간
    val error: String? = null
)
