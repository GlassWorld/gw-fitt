package com.gw.fitt.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercises")
data class ExerciseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val category: String,       // 상체 / 하체 / 코어 / 전신 / 유산소
    val defaultSets: Int,
    val defaultReps: Int,
    val durationSec: Int,
    val met: Double = 4.0
)
