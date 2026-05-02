package com.gw.fitt.presentation.routine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gw.fitt.domain.usecase.routine.CreateRoutineUseCase
import com.gw.fitt.domain.usecase.routine.DeleteRoutineUseCase
import com.gw.fitt.domain.usecase.routine.GetRoutinesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoutineViewModel @Inject constructor(
    private val getRoutinesUseCase: GetRoutinesUseCase,
    private val createRoutineUseCase: CreateRoutineUseCase,
    private val deleteRoutineUseCase: DeleteRoutineUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(RoutineState())
    val state: StateFlow<RoutineState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            getRoutinesUseCase().collect { routines ->
                _state.update { it.copy(routines = routines, isLoading = false) }
            }
        }
    }

    fun showCreateDialog() = _state.update { it.copy(showCreateDialog = true) }
    fun hideCreateDialog() = _state.update { it.copy(showCreateDialog = false) }

    fun createRoutine(name: String, level: String, estimatedMinutes: Int) {
        viewModelScope.launch {
            createRoutineUseCase(name.trim(), level, estimatedMinutes)
            hideCreateDialog()
        }
    }

    fun deleteRoutine(routineId: Int) {
        viewModelScope.launch {
            deleteRoutineUseCase(routineId)
        }
    }
}
