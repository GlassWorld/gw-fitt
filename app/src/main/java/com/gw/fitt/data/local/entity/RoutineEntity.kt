package com.gw.fitt.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "routines")
data class RoutineEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val level: String,              // 초급 / 중급 / 고급
    val estimatedMinutes: Int,
    val createdAt: Long = System.currentTimeMillis()
)
