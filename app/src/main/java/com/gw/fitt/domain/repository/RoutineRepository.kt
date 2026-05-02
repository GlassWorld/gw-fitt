package com.gw.fitt.domain.repository

import com.gw.fitt.domain.model.Routine
import com.gw.fitt.domain.model.RoutineExerciseInput
import com.gw.fitt.domain.model.RoutineWithExercises
import kotlinx.coroutines.flow.Flow

interface RoutineRepository {
    fun getAll(): Flow<List<Routine>>
    fun getById(id: Int): Flow<Routine?>
    fun getWithExercises(routineId: Int): Flow<RoutineWithExercises?>
    suspend fun insert(name: String, level: String, estimatedMinutes: Int): Long
    suspend fun replaceExercises(routineId: Int, exercises: List<RoutineExerciseInput>)
    suspend fun update(routine: Routine)
    suspend fun deleteById(id: Int)
}
