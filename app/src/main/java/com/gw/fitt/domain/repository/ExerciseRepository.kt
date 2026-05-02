package com.gw.fitt.domain.repository

import com.gw.fitt.domain.model.Exercise
import kotlinx.coroutines.flow.Flow

interface ExerciseRepository {
    fun getAll(): Flow<List<Exercise>>
    fun getByCategory(category: String): Flow<List<Exercise>>
    fun search(query: String): Flow<List<Exercise>>
    suspend fun insert(exercise: Exercise): Long
    suspend fun delete(exercise: Exercise)
}
