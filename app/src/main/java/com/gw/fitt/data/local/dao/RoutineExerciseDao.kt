package com.gw.fitt.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.gw.fitt.data.local.RoutineExerciseWithDetail
import com.gw.fitt.data.local.entity.RoutineExerciseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutineExerciseDao {

    @Query("""
        SELECT * FROM routine_exercises
        WHERE routineId = :routineId
        ORDER BY orderIndex
    """)
    fun getByRoutine(routineId: Int): Flow<List<RoutineExerciseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(routineExercise: RoutineExerciseEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(routineExercises: List<RoutineExerciseEntity>)

    @Query("DELETE FROM routine_exercises WHERE routineId = :routineId")
    suspend fun deleteByRoutine(routineId: Int)

    @Query("""
        DELETE FROM routine_exercises
        WHERE routineId = :routineId AND exerciseId = :exerciseId
    """)
    suspend fun delete(routineId: Int, exerciseId: Int)

    @Query("""
        SELECT re.routineId, re.exerciseId, re.orderIndex, re.customSets, re.customReps,
               e.name AS exerciseName, e.category, e.durationSec
        FROM routine_exercises re
        INNER JOIN exercises e ON re.exerciseId = e.id
        WHERE re.routineId = :routineId
        ORDER BY re.orderIndex
    """)
    fun getByRoutineWithDetail(routineId: Int): Flow<List<RoutineExerciseWithDetail>>

    // 루틴 운동 목록 전체 교체
    @Transaction
    suspend fun replaceAll(routineId: Int, routineExercises: List<RoutineExerciseEntity>) {
        deleteByRoutine(routineId)
        insertAll(routineExercises)
    }
}
