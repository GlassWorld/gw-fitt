package com.gw.fitt.presentation.routine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gw.fitt.domain.model.RoutineExerciseInput
import com.gw.fitt.domain.usecase.exercise.GetHomeExercisesUseCase
import com.gw.fitt.domain.usecase.exercise.SeedHomeExercisesUseCase
import com.gw.fitt.domain.usecase.routine.CreateRoutineUseCase
import com.gw.fitt.domain.usecase.routine.DeleteRoutineUseCase
import com.gw.fitt.domain.usecase.routine.GetRoutineDetailUseCase
import com.gw.fitt.domain.usecase.routine.GetRoutinesUseCase
import com.gw.fitt.domain.usecase.routine.SaveRoutineExercisesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoutineViewModel @Inject constructor(
    private val getRoutinesUseCase: GetRoutinesUseCase,
    private val getRoutineDetailUseCase: GetRoutineDetailUseCase,
    private val getHomeExercisesUseCase: GetHomeExercisesUseCase,
    private val seedHomeExercisesUseCase: SeedHomeExercisesUseCase,
    private val createRoutineUseCase: CreateRoutineUseCase,
    private val saveRoutineExercisesUseCase: SaveRoutineExercisesUseCase,
    private val deleteRoutineUseCase: DeleteRoutineUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(RoutineState())
    val state: StateFlow<RoutineState> = _state.asStateFlow()
    private var selectedRoutineJob: Job? = null

    init {
        viewModelScope.launch {
            seedHomeExercisesUseCase()
        }
        viewModelScope.launch {
            getRoutinesUseCase().collect { routines ->
                _state.update { it.copy(routines = routines, isLoading = false) }
            }
        }
        viewModelScope.launch {
            getHomeExercisesUseCase().collect { exercises ->
                _state.update { it.copy(homeExercises = exercises) }
            }
        }
    }

    fun showCreateDialog() = _state.update { it.copy(showCreateDialog = true) }
    fun hideCreateDialog() = _state.update { it.copy(showCreateDialog = false) }
    fun hideRoutineDetail() {
        selectedRoutineJob?.cancel()
        selectedRoutineJob = null
        _state.update { it.copy(selectedRoutine = null) }
    }

    fun showRoutineDetail(routineId: Int) {
        selectedRoutineJob?.cancel()
        selectedRoutineJob = viewModelScope.launch {
            getRoutineDetailUseCase(routineId).collect { detail ->
                _state.update { it.copy(selectedRoutine = detail) }
            }
        }
    }

    fun createRoutine(
        name: String,
        estimatedMinutes: Int,
        exercises: List<RoutineExerciseInput>
    ) {
        viewModelScope.launch {
            val routineId = createRoutineUseCase(name.trim(), "기본", estimatedMinutes).toInt()
            saveRoutineExercisesUseCase(routineId, exercises)
            hideCreateDialog()
        }
    }

    fun deleteRoutine(routineId: Int) {
        viewModelScope.launch {
            deleteRoutineUseCase(routineId)
        }
    }
}
