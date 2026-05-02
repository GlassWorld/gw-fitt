package com.gw.fitt.domain.model

data class RoutineWithExercises(
    val routine: Routine,
    val exercises: List<RoutineExercise>
)
