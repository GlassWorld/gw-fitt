package com.gw.fitt.presentation.timer

data class TimerState(
    val isRunning: Boolean = false,
    val isRestMode: Boolean = false,
    val elapsedSeconds: Long = 0L,
    val remainingRestSeconds: Long = 0L,
    val currentSet: Int = 1,
    val totalSets: Int = 3,
    val restDurationSeconds: Long = 60L,
    val isFinished: Boolean = false
)
