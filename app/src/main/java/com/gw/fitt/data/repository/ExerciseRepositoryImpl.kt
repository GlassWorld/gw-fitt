package com.gw.fitt.data.repository

import com.gw.fitt.data.local.dao.ExerciseDao
import com.gw.fitt.data.local.entity.ExerciseEntity
import com.gw.fitt.domain.model.Exercise
import com.gw.fitt.domain.repository.ExerciseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ExerciseRepositoryImpl @Inject constructor(
    private val exerciseDao: ExerciseDao
) : ExerciseRepository {

    override fun getAll(): Flow<List<Exercise>> =
        exerciseDao.getAll().map { list -> list.map { it.toDomain() } }

    override fun getByCategory(category: String): Flow<List<Exercise>> =
        exerciseDao.getByCategory(category).map { list -> list.map { it.toDomain() } }

    override fun search(query: String): Flow<List<Exercise>> =
        exerciseDao.search(query).map { list -> list.map { it.toDomain() } }

    override suspend fun insert(exercise: Exercise): Long =
        exerciseDao.insert(exercise.toEntity())

    override suspend fun insertAll(exercises: List<Exercise>) =
        exerciseDao.insertAll(exercises.map { it.toEntity() })

    override suspend fun delete(exercise: Exercise) =
        exerciseDao.delete(exercise.toEntity())
}

private fun ExerciseEntity.toDomain() = Exercise(
    id = id,
    name = name,
    category = category,
    defaultSets = defaultSets,
    defaultReps = defaultReps,
    durationSec = durationSec,
    met = met
)

private fun Exercise.toEntity() = ExerciseEntity(
    id = id,
    name = name,
    category = category,
    defaultSets = defaultSets,
    defaultReps = defaultReps,
    durationSec = durationSec,
    met = met
)
