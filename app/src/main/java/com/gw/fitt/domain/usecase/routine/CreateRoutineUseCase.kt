package com.gw.fitt.domain.usecase.routine

import com.gw.fitt.domain.repository.RoutineRepository
import javax.inject.Inject

class CreateRoutineUseCase @Inject constructor(
    private val routineRepository: RoutineRepository
) {
    suspend operator fun invoke(
        name: String,
        level: String,
        estimatedMinutes: Int
    ): Long = routineRepository.insert(name, level, estimatedMinutes)
}
