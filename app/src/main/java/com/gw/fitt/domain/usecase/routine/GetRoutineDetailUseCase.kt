package com.gw.fitt.domain.usecase.routine

import com.gw.fitt.domain.model.RoutineWithExercises
import com.gw.fitt.domain.repository.RoutineRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRoutineDetailUseCase @Inject constructor(
    private val routineRepository: RoutineRepository
) {
    operator fun invoke(routineId: Int): Flow<RoutineWithExercises?> =
        routineRepository.getWithExercises(routineId)
}
