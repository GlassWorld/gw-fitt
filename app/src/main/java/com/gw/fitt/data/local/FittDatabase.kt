package com.gw.fitt.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabase.Callback
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.gw.fitt.data.local.dao.ExerciseDao
import com.gw.fitt.data.local.dao.RoutineDao
import com.gw.fitt.data.local.dao.RoutineExerciseDao
import com.gw.fitt.data.local.dao.WorkoutLogDao
import com.gw.fitt.data.local.entity.ExerciseEntity
import com.gw.fitt.data.local.entity.RoutineEntity
import com.gw.fitt.data.local.entity.RoutineExerciseEntity
import com.gw.fitt.data.local.entity.WorkoutLogEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ExerciseEntity::class,
        RoutineEntity::class,
        RoutineExerciseEntity::class,
        WorkoutLogEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class FittDatabase : RoomDatabase() {

    abstract fun exerciseDao(): ExerciseDao
    abstract fun routineDao(): RoutineDao
    abstract fun routineExerciseDao(): RoutineExerciseDao
    abstract fun workoutLogDao(): WorkoutLogDao

    companion object {
        val prepopulateCallback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // DB가 처음 생성될 때 기본 운동 데이터 삽입
                CoroutineScope(Dispatchers.IO).launch {
                    db.execSQL(buildInsertSql(defaultExercises))
                }
            }
        }

        private fun buildInsertSql(exercises: List<ExerciseEntity>): String {
            val rows = exercises.joinToString(",\n") { e ->
                "('${e.name}', '${e.category}', ${e.defaultSets}, ${e.defaultReps}, ${e.durationSec}, ${e.met})"
            }
            return """
                INSERT OR IGNORE INTO exercises (name, category, defaultSets, defaultReps, durationSec, met)
                VALUES $rows
            """.trimIndent()
        }

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE exercises ADD COLUMN met REAL NOT NULL DEFAULT 4.0")
            }
        }

        private val defaultExercises = listOf(
            // 상체
            ExerciseEntity(name = "푸시업",         category = "상체", defaultSets = 3, defaultReps = 15, durationSec = 0),
            ExerciseEntity(name = "덤벨 숄더 프레스", category = "상체", defaultSets = 3, defaultReps = 12, durationSec = 0),
            ExerciseEntity(name = "바벨 벤치 프레스", category = "상체", defaultSets = 4, defaultReps = 10, durationSec = 0),
            // 하체
            ExerciseEntity(name = "스쿼트",          category = "하체", defaultSets = 4, defaultReps = 12, durationSec = 0),
            ExerciseEntity(name = "런지",             category = "하체", defaultSets = 3, defaultReps = 12, durationSec = 0),
            ExerciseEntity(name = "레그 프레스",      category = "하체", defaultSets = 3, defaultReps = 15, durationSec = 0),
            // 코어
            ExerciseEntity(name = "플랭크",           category = "코어", defaultSets = 3, defaultReps = 1,  durationSec = 60),
            ExerciseEntity(name = "크런치",           category = "코어", defaultSets = 3, defaultReps = 20, durationSec = 0),
            // 전신
            ExerciseEntity(name = "버피",             category = "전신", defaultSets = 3, defaultReps = 10, durationSec = 0),
            // 유산소
            ExerciseEntity(name = "줄넘기",           category = "유산소", defaultSets = 1, defaultReps = 1, durationSec = 300)
        )
    }
}
