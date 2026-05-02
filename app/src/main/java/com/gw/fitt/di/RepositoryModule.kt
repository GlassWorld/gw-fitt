package com.gw.fitt.di

import com.gw.fitt.data.repository.ExerciseRepositoryImpl
import com.gw.fitt.data.repository.RoutineRepositoryImpl
import com.gw.fitt.data.repository.WorkoutLogRepositoryImpl
import com.gw.fitt.domain.repository.ExerciseRepository
import com.gw.fitt.domain.repository.RoutineRepository
import com.gw.fitt.domain.repository.WorkoutLogRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindExerciseRepository(impl: ExerciseRepositoryImpl): ExerciseRepository

    @Binds
    @Singleton
    abstract fun bindRoutineRepository(impl: RoutineRepositoryImpl): RoutineRepository

    @Binds
    @Singleton
    abstract fun bindWorkoutLogRepository(impl: WorkoutLogRepositoryImpl): WorkoutLogRepository
}
