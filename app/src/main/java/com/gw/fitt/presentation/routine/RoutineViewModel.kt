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
    fun requestDeleteRoutine(routineId: Int) {
        val routine = _state.value.routines.firstOrNull { it.id == routineId } ?: return
        _state.update { it.copy(routinePendingDelete = routine) }
    }

    fun cancelDeleteRoutine() = _state.update { it.copy(routinePendingDelete = null) }

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

    fun updateRoutineExercises(routineId: Int, exercises: List<RoutineExerciseInput>) {
        viewModelScope.launch {
            saveRoutineExercisesUseCase(routineId, exercises)
        }
    }

    fun confirmDeleteRoutine() {
        val routineId = _state.value.routinePendingDelete?.id ?: return
        viewModelScope.launch {
            deleteRoutineUseCase(routineId)
            _state.update { it.copy(routinePendingDelete = null) }
        }
    }
}
