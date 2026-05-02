package com.gw.fitt.di

import android.content.Context
import androidx.room.Room
import com.gw.fitt.data.local.FittDatabase
import com.gw.fitt.data.local.dao.ExerciseDao
import com.gw.fitt.data.local.dao.RoutineDao
import com.gw.fitt.data.local.dao.RoutineExerciseDao
import com.gw.fitt.data.local.dao.WorkoutLogDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideFittDatabase(
        @ApplicationContext context: Context
    ): FittDatabase = Room.databaseBuilder(
        context,
        FittDatabase::class.java,
        "fitt.db"
    )
        .addCallback(FittDatabase.prepopulateCallback)
        .build()

    @Provides
    @Singleton
    fun provideExerciseDao(db: FittDatabase): ExerciseDao = db.exerciseDao()

    @Provides
    @Singleton
    fun provideRoutineDao(db: FittDatabase): RoutineDao = db.routineDao()

    @Provides
    @Singleton
    fun provideRoutineExerciseDao(db: FittDatabase): RoutineExerciseDao = db.routineExerciseDao()

    @Provides
    @Singleton
    fun provideWorkoutLogDao(db: FittDatabase): WorkoutLogDao = db.workoutLogDao()
}
