package com.gw.fitt.presentation.timer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimerViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(TimerState())
    val state: StateFlow<TimerState> = _state.asStateFlow()

    private var timerJob: Job? = null

    fun toggleTimer() {
        if (_state.value.isRunning) pauseTimer() else startWorkoutTimer()
    }

    fun completeSet() {
        val s = _state.value
        if (s.currentSet >= s.totalSets) {
            timerJob?.cancel()
            _state.update { it.copy(isRunning = false, isFinished = true) }
            return
        }
        timerJob?.cancel()
        _state.update { it.copy(
            isRunning = false,
            isRestMode = true,
            remainingRestSeconds = it.restDurationSeconds
        )}
        startRestTimer()
    }

    fun reset() {
        timerJob?.cancel()
        _state.value = TimerState()
    }

    fun setTotalSets(count: Int) {
        if (!_state.value.isRunning && !_state.value.isRestMode) {
            _state.update { it.copy(totalSets = count) }
        }
    }

    fun setRestDuration(seconds: Long) {
        if (!_state.value.isRunning && !_state.value.isRestMode) {
            _state.update { it.copy(restDurationSeconds = seconds) }
        }
    }

    private fun startWorkoutTimer() {
        _state.update { it.copy(isRunning = true, isRestMode = false) }
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1_000)
                _state.update { it.copy(elapsedSeconds = it.elapsedSeconds + 1) }
            }
        }
    }

    private fun pauseTimer() {
        timerJob?.cancel()
        _state.update { it.copy(isRunning = false) }
    }

    private fun startRestTimer() {
        timerJob = viewModelScope.launch {
            while (_state.value.remainingRestSeconds > 0) {
                delay(1_000)
                _state.update { it.copy(remainingRestSeconds = it.remainingRestSeconds - 1) }
            }
            _state.update { it.copy(
                isRestMode = false,
                currentSet = it.currentSet + 1,
                elapsedSeconds = 0L
            )}
            startWorkoutTimer()
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
