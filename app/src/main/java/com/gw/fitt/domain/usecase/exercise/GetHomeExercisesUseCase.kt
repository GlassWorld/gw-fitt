package com.gw.fitt.domain.usecase.exercise

import com.gw.fitt.domain.model.Exercise
import com.gw.fitt.domain.repository.ExerciseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetHomeExercisesUseCase @Inject constructor(
    private val exerciseRepository: ExerciseRepository
) {
    operator fun invoke(): Flow<List<Exercise>> =
        exerciseRepository.getByCategory(SeedHomeExercisesUseCase.HOME_CATEGORY)
}
