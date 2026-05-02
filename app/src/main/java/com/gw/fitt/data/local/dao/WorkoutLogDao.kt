package com.gw.fitt.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gw.fitt.data.local.entity.WorkoutLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutLogDao {

    @Query("SELECT * FROM workout_logs ORDER BY startedAt DESC")
    fun getAll(): Flow<List<WorkoutLogEntity>>

    @Query("SELECT * FROM workout_logs WHERE routineId = :routineId ORDER BY startedAt DESC")
    fun getByRoutine(routineId: Int): Flow<List<WorkoutLogEntity>>

    @Query("SELECT * FROM workout_logs WHERE id = :id")
    fun getById(id: Int): Flow<WorkoutLogEntity?>

    // 기간별 조회 (홈 화면 주간/월간 통계용)
    @Query("""
        SELECT * FROM workout_logs
        WHERE startedAt BETWEEN :from AND :to
        ORDER BY startedAt DESC
    """)
    fun getByDateRange(from: Long, to: Long): Flow<List<WorkoutLogEntity>>

    @Query("""
        SELECT SUM(durationMinutes) FROM workout_logs
        WHERE startedAt BETWEEN :from AND :to
    """)
    fun getTotalMinutesBetween(from: Long, to: Long): Flow<Int?>

    @Query("""
        SELECT SUM(totalCalories) FROM workout_logs
        WHERE startedAt BETWEEN :from AND :to
    """)
    fun getTotalCaloriesBetween(from: Long, to: Long): Flow<Int?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: WorkoutLogEntity): Long

    @Delete
    suspend fun delete(log: WorkoutLogEntity)

    @Query("DELETE FROM workout_logs WHERE id = :id")
    suspend fun deleteById(id: Int)
}
