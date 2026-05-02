package com.gw.fitt.presentation.routine

import com.gw.fitt.domain.model.Exercise
import com.gw.fitt.domain.model.Routine
import com.gw.fitt.domain.model.RoutineWithExercises

data class RoutineState(
    val isLoading: Boolean = true,
    val routines: List<Routine> = emptyList(),
    val homeExercises: List<Exercise> = emptyList(),
    val showCreateDialog: Boolean = false,
    val selectedRoutine: RoutineWithExercises? = null,
    val error: String? = null
)
