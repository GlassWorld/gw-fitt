package com.gw.fitt.data.local

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

data class SavedWorkoutSession(
    val routineId: Int,
    val routineName: String,
    val totalSets: Int,
    val currentSet: Int,
    val elapsedSeconds: Long,
    val totalElapsedSeconds: Long,
    val restDurationSeconds: Long,
    val workoutStartedAt: Long
)

@Singleton
class WorkoutSessionStore @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs = context.getSharedPreferences("workout_session", Context.MODE_PRIVATE)

    fun get(routineId: Int): SavedWorkoutSession? {
        if (!prefs.getBoolean(KEY_ACTIVE, false)) return null
        if (prefs.getInt(KEY_ROUTINE_ID, 0) != routineId) return null

        return SavedWorkoutSession(
            routineId = prefs.getInt(KEY_ROUTINE_ID, 0),
            routineName = prefs.getString(KEY_ROUTINE_NAME, null) ?: return null,
            totalSets = prefs.getInt(KEY_TOTAL_SETS, 1),
            currentSet = prefs.getInt(KEY_CURRENT_SET, 1),
            elapsedSeconds = prefs.getLong(KEY_ELAPSED_SECONDS, 0L),
            totalElapsedSeconds = prefs.getLong(KEY_TOTAL_ELAPSED_SECONDS, 0L),
            restDurationSeconds = prefs.getLong(KEY_REST_DURATION_SECONDS, 60L),
            workoutStartedAt = prefs.getLong(KEY_WORKOUT_STARTED_AT, 0L).takeIf { it > 0L } ?: return null
        )
    }

    fun save(session: SavedWorkoutSession) {
        prefs.edit {
            putBoolean(KEY_ACTIVE, true)
            putInt(KEY_ROUTINE_ID, session.routineId)
            putString(KEY_ROUTINE_NAME, session.routineName)
            putInt(KEY_TOTAL_SETS, session.totalSets)
            putInt(KEY_CURRENT_SET, session.currentSet)
            putLong(KEY_ELAPSED_SECONDS, session.elapsedSeconds)
            putLong(KEY_TOTAL_ELAPSED_SECONDS, session.totalElapsedSeconds)
            putLong(KEY_REST_DURATION_SECONDS, session.restDurationSeconds)
            putLong(KEY_WORKOUT_STARTED_AT, session.workoutStartedAt)
        }
    }

    fun clear() {
        prefs.edit {
            remove(KEY_ACTIVE)
            remove(KEY_ROUTINE_ID)
            remove(KEY_ROUTINE_NAME)
            remove(KEY_TOTAL_SETS)
            remove(KEY_CURRENT_SET)
            remove(KEY_ELAPSED_SECONDS)
            remove(KEY_TOTAL_ELAPSED_SECONDS)
            remove(KEY_REST_DURATION_SECONDS)
            remove(KEY_WORKOUT_STARTED_AT)
        }
    }

    fun getLastWeightKg(): Double? =
        prefs.getFloat(KEY_LAST_WEIGHT_KG, 0f).takeIf { it > 0f }?.toDouble()

    fun saveLastWeightKg(weightKg: Double) {
        prefs.edit { putFloat(KEY_LAST_WEIGHT_KG, weightKg.toFloat()) }
    }

    private companion object {
        const val KEY_ACTIVE = "active"
        const val KEY_ROUTINE_ID = "routine_id"
        const val KEY_ROUTINE_NAME = "routine_name"
        const val KEY_TOTAL_SETS = "total_sets"
        const val KEY_CURRENT_SET = "current_set"
        const val KEY_ELAPSED_SECONDS = "elapsed_seconds"
        const val KEY_TOTAL_ELAPSED_SECONDS = "total_elapsed_seconds"
        const val KEY_REST_DURATION_SECONDS = "rest_duration_seconds"
        const val KEY_WORKOUT_STARTED_AT = "workout_started_at"
        const val KEY_LAST_WEIGHT_KG = "last_weight_kg"
    }
}
