package com.gw.fitt.navigation

import com.gw.fitt.domain.model.RoutineExercise

object SelectedWorkoutHolder {
    private var exercises: List<RoutineExercise> = emptyList()

    fun set(value: List<RoutineExercise>) {
        exercises = value
    }

    fun consume(): List<RoutineExercise> {
        val current = exercises
        exercises = emptyList()
        return current
    }
}
