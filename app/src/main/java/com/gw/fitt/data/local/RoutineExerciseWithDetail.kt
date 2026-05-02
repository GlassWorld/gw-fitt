package com.gw.fitt.data.local

// Room JOIN 쿼리 결과 매핑용 클래스
data class RoutineExerciseWithDetail(
    val routineId: Int,
    val exerciseId: Int,
    val orderIndex: Int,
    val customSets: Int,
    val customReps: Int,
    val exerciseName: String,
    val category: String,
    val durationSec: Int
)
