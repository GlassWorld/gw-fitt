package com.gw.fitt.domain.usecase.log

import com.gw.fitt.domain.model.WeeklyStats
import com.gw.fitt.domain.repository.WorkoutLogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetWeeklyStatsUseCase @Inject constructor(
    private val workoutLogRepository: WorkoutLogRepository
) {
    operator fun invoke(): Flow<WeeklyStats> {
        val now = System.currentTimeMillis()
        val weekAgo = now - 7L * 24 * 60 * 60 * 1000

        return combine(
            workoutLogRepository.getByDateRange(weekAgo, now),
            workoutLogRepository.getTotalMinutesBetween(weekAgo, now),
            workoutLogRepository.getTotalCaloriesBetween(weekAgo, now)
        ) { logs, minutes, calories ->
            WeeklyStats(
                workoutCount = logs.size,
                totalMinutes = minutes ?: 0,
                totalCalories = calories ?: 0
            )
        }
    }
}
