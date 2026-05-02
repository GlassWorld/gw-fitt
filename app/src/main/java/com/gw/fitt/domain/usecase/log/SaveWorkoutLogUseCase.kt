package com.gw.fitt.domain.usecase.log

import com.gw.fitt.domain.model.WorkoutLog
import com.gw.fitt.domain.repository.WorkoutLogRepository
import javax.inject.Inject

class SaveWorkoutLogUseCase @Inject constructor(
    private val workoutLogRepository: WorkoutLogRepository
) {
    suspend operator fun invoke(log: WorkoutLog): Long = workoutLogRepository.insert(log)
}
