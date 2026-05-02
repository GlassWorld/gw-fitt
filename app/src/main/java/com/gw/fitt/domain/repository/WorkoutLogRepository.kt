package com.gw.fitt.domain.repository

import com.gw.fitt.domain.model.WorkoutLog
import kotlinx.coroutines.flow.Flow

interface WorkoutLogRepository {
    fun getAll(): Flow<List<WorkoutLog>>
    fun getByDateRange(from: Long, to: Long): Flow<List<WorkoutLog>>
    fun getTotalMinutesBetween(from: Long, to: Long): Flow<Int?>
    fun getTotalCaloriesBetween(from: Long, to: Long): Flow<Int?>
    suspend fun insert(log: WorkoutLog): Long
    suspend fun deleteById(id: Int)
}
