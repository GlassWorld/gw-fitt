package com.gw.fitt.domain.model

data class WorkoutLog(
    val id: Int,
    val routineId: Int,
    val routineName: String,
    val startedAt: Long,
    val finishedAt: Long,
    val durationMinutes: Int,
    val totalCalories: Int
)
