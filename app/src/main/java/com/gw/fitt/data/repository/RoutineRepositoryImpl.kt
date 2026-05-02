package com.gw.fitt.data.repository

import com.gw.fitt.data.local.RoutineExerciseWithDetail
import com.gw.fitt.data.local.dao.RoutineDao
import com.gw.fitt.data.local.dao.RoutineExerciseDao
import com.gw.fitt.data.local.entity.RoutineEntity
import com.gw.fitt.domain.model.Routine
import com.gw.fitt.domain.model.RoutineExercise
import com.gw.fitt.domain.model.RoutineWithExercises
import com.gw.fitt.domain.repository.RoutineRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RoutineRepositoryImpl @Inject constructor(
    private val routineDao: RoutineDao,
    private val routineExerciseDao: RoutineExerciseDao
) : RoutineRepository {

    override fun getAll(): Flow<List<Routine>> =
        routineDao.getAll().map { list -> list.map { it.toDomain() } }

    override fun getById(id: Int): Flow<Routine?> =
        routineDao.getById(id).map { it?.toDomain() }

    override fun getWithExercises(routineId: Int): Flow<RoutineWithExercises?> = combine(
        routineDao.getById(routineId),
        routineExerciseDao.getByRoutineWithDetail(routineId)
    ) { routineEntity, exerciseDetails ->
        routineEntity?.let {
            RoutineWithExercises(
                routine = it.toDomain(),
                exercises = exerciseDetails.map { detail -> detail.toDomain() }
            )
        }
    }

    override suspend fun insert(name: String, level: String, estimatedMinutes: Int): Long =
        routineDao.insert(
            RoutineEntity(
                name = name,
                level = level,
                estimatedMinutes = estimatedMinutes
            )
        )

    override suspend fun update(routine: Routine) =
        routineDao.update(routine.toEntity())

    override suspend fun deleteById(id: Int) =
        routineDao.deleteById(id)
}

private fun RoutineEntity.toDomain() = Routine(
    id = id,
    name = name,
    level = level,
    estimatedMinutes = estimatedMinutes,
    createdAt = createdAt
)

private fun Routine.toEntity() = RoutineEntity(
    id = id,
    name = name,
    level = level,
    estimatedMinutes = estimatedMinutes,
    createdAt = createdAt
)

private fun RoutineExerciseWithDetail.toDomain() = RoutineExercise(
    routineId = routineId,
    exerciseId = exerciseId,
    exerciseName = exerciseName,
    category = category,
    orderIndex = orderIndex,
    customSets = customSets,
    customReps = customReps,
    durationSec = durationSec
)
