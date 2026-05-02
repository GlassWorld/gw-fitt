package com.gw.fitt.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_logs")
data class WorkoutLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val routineId: Int,
    val routineName: String,
    val startedAt: Long,
    val finishedAt: Long,
    val durationMinutes: Int,
    val totalCalories: Int
)
