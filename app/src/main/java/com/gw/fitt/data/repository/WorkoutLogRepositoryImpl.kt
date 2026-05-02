package com.gw.fitt.data.repository

import com.gw.fitt.data.local.dao.WorkoutLogDao
import com.gw.fitt.data.local.entity.WorkoutLogEntity
import com.gw.fitt.domain.model.WorkoutLog
import com.gw.fitt.domain.repository.WorkoutLogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WorkoutLogRepositoryImpl @Inject constructor(
    private val workoutLogDao: WorkoutLogDao
) : WorkoutLogRepository {

    override fun getAll(): Flow<List<WorkoutLog>> =
        workoutLogDao.getAll().map { list -> list.map { it.toDomain() } }

    override fun getByDateRange(from: Long, to: Long): Flow<List<WorkoutLog>> =
        workoutLogDao.getByDateRange(from, to).map { list -> list.map { it.toDomain() } }

    override fun getTotalMinutesBetween(from: Long, to: Long): Flow<Int?> =
        workoutLogDao.getTotalMinutesBetween(from, to)

    override fun getTotalCaloriesBetween(from: Long, to: Long): Flow<Int?> =
        workoutLogDao.getTotalCaloriesBetween(from, to)

    override suspend fun insert(log: WorkoutLog): Long =
        workoutLogDao.insert(log.toEntity())

    override suspend fun deleteById(id: Int) =
        workoutLogDao.deleteById(id)
}

private fun WorkoutLogEntity.toDomain() = WorkoutLog(
    id = id,
    routineId = routineId,
    routineName = routineName,
    startedAt = startedAt,
    finishedAt = finishedAt,
    durationMinutes = durationMinutes,
    totalCalories = totalCalories
)

private fun WorkoutLog.toEntity() = WorkoutLogEntity(
    id = id,
    routineId = routineId,
    routineName = routineName,
    startedAt = startedAt,
    finishedAt = finishedAt,
    durationMinutes = durationMinutes,
    totalCalories = totalCalories
)
