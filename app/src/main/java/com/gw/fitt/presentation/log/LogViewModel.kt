package com.gw.fitt.presentation.log

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gw.fitt.domain.model.WorkoutLog
import com.gw.fitt.domain.usecase.log.GetWeeklyStatsUseCase
import com.gw.fitt.domain.usecase.log.GetWorkoutLogsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class LogViewModel @Inject constructor(
    private val getWorkoutLogsUseCase: GetWorkoutLogsUseCase,
    private val getWeeklyStatsUseCase: GetWeeklyStatsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(LogState())
    val state: StateFlow<LogState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                getWorkoutLogsUseCase(),
                getWeeklyStatsUseCase()
            ) { logs, stats ->
                LogState(
                    isLoading = false,
                    logs = logs,
                    weeklyStats = stats,
                    dailyMinutes = computeDailyMinutes(logs)
                )
            }.collect { _state.value = it }
        }
    }

    // 최근 7일 일별 운동 시간 집계 (index 0 = 6일 전, index 6 = 오늘)
    private fun computeDailyMinutes(logs: List<WorkoutLog>): List<Float> {
        val cal = Calendar.getInstance()
        val today = cal.get(Calendar.DAY_OF_YEAR)
        val year = cal.get(Calendar.YEAR)
        val result = MutableList(7) { 0f }

        logs.forEach { log ->
            cal.timeInMillis = log.startedAt
            val logYear = cal.get(Calendar.YEAR)
            val logDay = cal.get(Calendar.DAY_OF_YEAR)
            val daysAgo = if (logYear == year) today - logDay else -1
            if (daysAgo in 0..6) result[6 - daysAgo] += log.durationMinutes.toFloat()
        }
        return result
    }
}
