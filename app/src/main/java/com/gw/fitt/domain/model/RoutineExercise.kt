package com.gw.fitt.domain.model

data class RoutineExercise(
    val routineId: Int,
    val exerciseId: Int,
    val exerciseName: String,
    val category: String,
    val orderIndex: Int,
    val customSets: Int,
    val customReps: Int,
    val durationSec: Int,
    val met: Double = 4.0
)
