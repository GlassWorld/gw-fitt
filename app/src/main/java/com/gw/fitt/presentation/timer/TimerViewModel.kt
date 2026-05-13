package com.gw.fitt.presentation.timer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gw.fitt.data.local.SavedWorkoutSession
import com.gw.fitt.data.local.WorkoutSessionStore
import com.gw.fitt.domain.model.RoutineExercise
import com.gw.fitt.domain.model.WorkoutLog
import com.gw.fitt.domain.usecase.log.SaveWorkoutLogUseCase
import com.gw.fitt.domain.usecase.routine.GetRoutineDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.ceil
import kotlin.math.max

@HiltViewModel
class TimerViewModel @Inject constructor(
    private val getRoutineDetailUseCase: GetRoutineDetailUseCase,
    private val saveWorkoutLogUseCase: SaveWorkoutLogUseCase,
    private val workoutSessionStore: WorkoutSessionStore
) : ViewModel() {

    private val _state = MutableStateFlow(TimerState())
    val state: StateFlow<TimerState> = _state.asStateFlow()

    private var timerJob: Job? = null
    private var routineJob: Job? = null

    fun configureRoutine(routineId: Int?, routineName: String?, totalSets: Int) {
        val current = _state.value
        if (routineId == null && routineName.isNullOrBlank() && current.routineId == null && current.routineExercises.isNotEmpty()) return
        if (current.routineId == routineId && current.routineName == routineName && current.totalSets == totalSets) return
        routineJob?.cancel()
        _state.value = TimerState(
            routineId = routineId?.takeIf { it > 0 },
            routineName = routineName?.takeIf { it.isNotBlank() },
            totalSets = totalSets.coerceAtLeast(1),
            lastWeightKg = workoutSessionStore.getLastWeightKg()
        )
        val id = routineId?.takeIf { it > 0 } ?: return
        restoreSessionIfPresent(id)
        routineJob = viewModelScope.launch {
            getRoutineDetailUseCase(id).collect { detail ->
                if (detail != null) {
                    _state.update {
                        it.copy(
                            routineName = detail.routine.name,
                            routineExercises = detail.exercises,
                            totalSets = detail.exercises.sumOf { exercise -> exercise.customSets }.coerceAtLeast(1)
                        )
                    }
                }
            }
        }
    }

    fun startSelectedWorkout(exercises: List<RoutineExercise>) {
        if (exercises.isEmpty()) return
        routineJob?.cancel()
        timerJob?.cancel()
        _state.value = TimerState(
            routineName = "선택 운동",
            routineExercises = exercises,
            totalSets = exercises.sumOf { it.customSets }.coerceAtLeast(1),
            restDurationSeconds = _state.value.restDurationSeconds,
            lastWeightKg = workoutSessionStore.getLastWeightKg()
        )
    }

    fun toggleTimer() {
        if (_state.value.isRunning) pauseTimer() else startWorkoutTimer()
    }

    fun completeSet() {
        val s = _state.value
        if (s.currentSet >= s.totalSets) {
            timerJob?.cancel()
            _state.update {
                it.copy(
                    isRunning = false,
                    isFinished = true,
                    isAwaitingWeight = it.routineId != null,
                    isLogSaved = false
                )
            }
            workoutSessionStore.clear()
            if (s.routineId == null) saveLogIfNeeded(weightKg = null)
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

    fun stopAndSaveForLater() {
        val s = _state.value
        timerJob?.cancel()
        saveSession(s)
        _state.update {
            it.copy(
                isRunning = false,
                isRestMode = false,
                isPausedForLater = true
            )
        }
    }

    fun abandonWorkout() {
        timerJob?.cancel()
        if (_state.value.routineId != null) {
            workoutSessionStore.clear()
        }
        if (_state.value.routineId == null) {
            _state.value = TimerState(
                restDurationSeconds = _state.value.restDurationSeconds,
                lastWeightKg = _state.value.lastWeightKg
            )
            return
        }
        _state.value = TimerState(
            routineId = _state.value.routineId,
            routineName = _state.value.routineName,
            routineExercises = _state.value.routineExercises,
            totalSets = _state.value.totalSets,
            restDurationSeconds = _state.value.restDurationSeconds
        )
    }

    fun reset() {
        timerJob?.cancel()
        if (_state.value.routineId != null) {
            workoutSessionStore.clear()
        }
        _state.value = TimerState(
            routineId = _state.value.routineId,
            routineName = _state.value.routineName,
            routineExercises = _state.value.routineExercises,
            totalSets = _state.value.totalSets,
            restDurationSeconds = _state.value.restDurationSeconds,
            lastWeightKg = _state.value.lastWeightKg
        )
    }

    fun setTotalSets(count: Int) {
        if (!_state.value.isRunning && !_state.value.isRestMode && _state.value.routineId == null) {
            _state.update { it.copy(totalSets = count) }
        }
    }

    fun setRestDuration(seconds: Long) {
        if (!_state.value.isRunning && !_state.value.isRestMode) {
            _state.update { it.copy(restDurationSeconds = seconds) }
        }
    }

    private fun startWorkoutTimer() {
        _state.update {
            it.copy(
                isRunning = true,
                isRestMode = false,
                isPausedForLater = false,
                workoutStartedAt = it.workoutStartedAt ?: System.currentTimeMillis()
            )
        }
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1_000)
                _state.update {
                    it.copy(
                        elapsedSeconds = it.elapsedSeconds + 1,
                        totalElapsedSeconds = it.totalElapsedSeconds + 1
                    )
                }
            }
        }
    }

    private fun pauseTimer() {
        timerJob?.cancel()
        _state.update { it.copy(isRunning = false) }
        saveSession(_state.value)
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
            saveSession(_state.value)
            startWorkoutTimer()
        }
    }

    private fun restoreSessionIfPresent(routineId: Int) {
        val saved = workoutSessionStore.get(routineId) ?: return
        _state.update {
            it.copy(
                routineId = saved.routineId,
                routineName = saved.routineName,
                totalSets = saved.totalSets,
                currentSet = saved.currentSet,
                elapsedSeconds = saved.elapsedSeconds,
                totalElapsedSeconds = saved.totalElapsedSeconds,
                restDurationSeconds = saved.restDurationSeconds,
                workoutStartedAt = saved.workoutStartedAt,
                isRunning = false,
                isRestMode = false,
                isPausedForLater = true,
                hasRestoredSession = true
            )
        }
    }

    private fun saveSession(state: TimerState) {
        val routineId = state.routineId ?: return
        val routineName = state.routineName ?: return
        val startedAt = state.workoutStartedAt ?: System.currentTimeMillis()
        workoutSessionStore.save(
            SavedWorkoutSession(
                routineId = routineId,
                routineName = routineName,
                totalSets = state.totalSets,
                currentSet = state.currentSet,
                elapsedSeconds = state.elapsedSeconds,
                totalElapsedSeconds = state.totalElapsedSeconds,
                restDurationSeconds = state.restDurationSeconds,
                workoutStartedAt = startedAt
            )
        )
    }

    fun saveCompletedWorkout(weightKg: Double) {
        workoutSessionStore.saveLastWeightKg(weightKg)
        saveLogIfNeeded(weightKg)
    }

    private fun saveLogIfNeeded(weightKg: Double?) {
        val s = _state.value
        val routineId = s.routineId ?: 0
        val routineName = s.routineName ?: "자유 타이머"
        if (s.isLogSaved) return

        viewModelScope.launch {
            val finishedAt = System.currentTimeMillis()
            val startedAt = s.workoutStartedAt ?: finishedAt
            val durationMinutes = max(1, ceil(s.totalElapsedSeconds / 60.0).toInt())
            val calories = weightKg?.let {
                calculateCalories(weightKg = it, durationMinutes = durationMinutes, exercises = s.routineExercises)
            } ?: durationMinutes * 5
            saveWorkoutLogUseCase(
                WorkoutLog(
                    id = 0,
                    routineId = routineId,
                    routineName = routineName,
                    startedAt = startedAt,
                    finishedAt = finishedAt,
                    durationMinutes = durationMinutes,
                    totalCalories = calories
                )
            )
            workoutSessionStore.clear()
            _state.update {
                it.copy(
                    isLogSaved = true,
                    isAwaitingWeight = false,
                    lastWeightKg = weightKg ?: it.lastWeightKg,
                    estimatedCalories = calories
                )
            }
        }
    }

    private fun calculateCalories(
        weightKg: Double,
        durationMinutes: Int,
        exercises: List<com.gw.fitt.domain.model.RoutineExercise>
    ): Int {
        val averageMet = exercises
            .takeIf { it.isNotEmpty() }
            ?.let { list -> list.sumOf { it.met * it.customSets } / list.sumOf { it.customSets }.coerceAtLeast(1) }
            ?: 4.0
        return max(1, (averageMet * weightKg * (durationMinutes / 60.0)).toInt())
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        routineJob?.cancel()
    }
}
