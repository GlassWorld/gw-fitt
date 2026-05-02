package com.gw.fitt.domain.usecase.log

import com.gw.fitt.domain.model.WorkoutLog
import com.gw.fitt.domain.repository.WorkoutLogRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWorkoutLogsUseCase @Inject constructor(
    private val workoutLogRepository: WorkoutLogRepository
) {
    operator fun invoke(): Flow<List<WorkoutLog>> = workoutLogRepository.getAll()
}
