package com.gw.fitt.domain.usecase.routine

import com.gw.fitt.domain.repository.RoutineRepository
import javax.inject.Inject

class DeleteRoutineUseCase @Inject constructor(
    private val routineRepository: RoutineRepository
) {
    suspend operator fun invoke(routineId: Int) = routineRepository.deleteById(routineId)
}
