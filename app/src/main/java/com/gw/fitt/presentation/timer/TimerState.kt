package com.gw.fitt.presentation.timer

import com.gw.fitt.domain.model.RoutineExercise

data class TimerState(
    val isRunning: Boolean = false,
    val isRestMode: Boolean = false,
    val elapsedSeconds: Long = 0L,
    val totalElapsedSeconds: Long = 0L,
    val remainingRestSeconds: Long = 0L,
    val currentSet: Int = 1,
    val totalSets: Int = 3,
    val restDurationSeconds: Long = 60L,
    val isFinished: Boolean = false,
    val routineId: Int? = null,
    val routineName: String? = null,
    val routineExercises: List<RoutineExercise> = emptyList(),
    val workoutStartedAt: Long? = null,
    val isLogSaved: Boolean = false,
    val isAwaitingWeight: Boolean = false,
    val lastWeightKg: Double? = null,
    val estimatedCalories: Int? = null,
    val isPausedForLater: Boolean = false,
    val hasRestoredSession: Boolean = false
)
