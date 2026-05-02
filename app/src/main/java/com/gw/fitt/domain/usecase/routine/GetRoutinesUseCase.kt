package com.gw.fitt.domain.usecase.routine

import com.gw.fitt.domain.model.Routine
import com.gw.fitt.domain.repository.RoutineRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRoutinesUseCase @Inject constructor(
    private val routineRepository: RoutineRepository
) {
    operator fun invoke(): Flow<List<Routine>> = routineRepository.getAll()
}
