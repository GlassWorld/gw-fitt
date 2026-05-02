package com.gw.fitt.domain.usecase.routine

import com.gw.fitt.domain.model.RoutineExerciseInput
import com.gw.fitt.domain.repository.RoutineRepository
import javax.inject.Inject

class SaveRoutineExercisesUseCase @Inject constructor(
    private val routineRepository: RoutineRepository
) {
    suspend operator fun invoke(routineId: Int, exercises: List<RoutineExerciseInput>) {
        routineRepository.replaceExercises(routineId, exercises)
    }
}
