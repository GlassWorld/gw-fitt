package com.gw.fitt.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gw.fitt.data.local.WorkoutSessionStore
import com.gw.fitt.domain.usecase.log.GetWeeklyStatsUseCase
import com.gw.fitt.domain.usecase.routine.GetRoutinesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getWeeklyStatsUseCase: GetWeeklyStatsUseCase,
    private val getRoutinesUseCase: GetRoutinesUseCase,
    private val workoutSessionStore: WorkoutSessionStore
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            combine(
                getWeeklyStatsUseCase(),
                getRoutinesUseCase()
            ) { stats, routines ->
                HomeState(
                    isLoading = false,
                    weeklyStats = stats,
                    recentRoutines = routines.take(3),
                    weightKg = workoutSessionStore.getLastWeightKg()
                )
            }.collect { newState ->
                _state.value = newState
            }
        }
    }

    fun saveWeight(weightKg: Double) {
        workoutSessionStore.saveLastWeightKg(weightKg)
        _state.update { it.copy(weightKg = weightKg) }
    }
}
