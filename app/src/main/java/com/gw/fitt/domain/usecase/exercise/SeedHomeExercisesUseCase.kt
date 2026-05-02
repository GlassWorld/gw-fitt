package com.gw.fitt.domain.usecase.exercise

import com.gw.fitt.domain.model.Exercise
import com.gw.fitt.domain.repository.ExerciseRepository
import javax.inject.Inject

class SeedHomeExercisesUseCase @Inject constructor(
    private val exerciseRepository: ExerciseRepository
) {
    suspend operator fun invoke() {
        exerciseRepository.insertAll(homeExercises)
    }

    companion object {
        const val HOME_CATEGORY = "맨몸운동"

        val homeExercises = listOf(
            Exercise(id = 101, name = "스쿼트", category = HOME_CATEGORY, defaultSets = 3, defaultReps = 15, durationSec = 0, met = 5.0),
            Exercise(id = 102, name = "윗몸일으키기", category = HOME_CATEGORY, defaultSets = 3, defaultReps = 20, durationSec = 0, met = 3.8),
            Exercise(id = 103, name = "런지", category = HOME_CATEGORY, defaultSets = 3, defaultReps = 12, durationSec = 0, met = 4.5),
            Exercise(id = 104, name = "팔굽혀펴기", category = HOME_CATEGORY, defaultSets = 3, defaultReps = 12, durationSec = 0, met = 4.0),
            Exercise(id = 105, name = "플랭크", category = HOME_CATEGORY, defaultSets = 3, defaultReps = 1, durationSec = 45, met = 3.3),
            Exercise(id = 106, name = "버피", category = HOME_CATEGORY, defaultSets = 3, defaultReps = 10, durationSec = 0, met = 8.0),
            Exercise(id = 107, name = "마운틴 클라이머", category = HOME_CATEGORY, defaultSets = 3, defaultReps = 20, durationSec = 0, met = 8.0),
            Exercise(id = 108, name = "글루트 브릿지", category = HOME_CATEGORY, defaultSets = 3, defaultReps = 15, durationSec = 0, met = 3.5)
        )
    }
}
