package com.gw.fitt.presentation.timer

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gw.fitt.domain.model.RoutineExercise
import com.gw.fitt.ui.component.FittTopBar
import com.gw.fitt.ui.theme.fittColors

private val setOptions = listOf(1, 2, 3, 4, 5)
private val restOptions = listOf(30L to "30초", 60L to "1분", 90L to "1분 30초", 120L to "2분")

@Composable
fun TimerScreen(
    routineId: Int? = null,
    routineName: String? = null,
    totalSets: Int = 3,
    selectedExercises: List<RoutineExercise> = emptyList(),
    onBackToSelection: (() -> Unit)? = null,
    viewModel: TimerViewModel = hiltViewModel()
) {
    LaunchedEffect(routineId, routineName, totalSets) {
        viewModel.configureRoutine(routineId, routineName, totalSets)
    }
    LaunchedEffect(selectedExercises) {
        if (selectedExercises.isNotEmpty()) {
            viewModel.startSelectedWorkout(selectedExercises)
        }
    }

    val state by viewModel.state.collectAsState()
    val accent = MaterialTheme.fittColors.accent

    if (state.isAwaitingWeight) {
        WeightInputDialog(
            initialWeightKg = state.lastWeightKg,
            onSave = viewModel::saveCompletedWorkout
        )
    }

    val canReturnToSelection = state.routineId == null &&
        state.routineName == "선택 운동" &&
        state.routineExercises.isNotEmpty() &&
        onBackToSelection != null

    Scaffold(
        topBar = {
            FittTopBar(
                title = state.routineName ?: "타이머",
                onNavigateBack = if (canReturnToSelection) {
                    {
                        viewModel.abandonWorkout()
                        onBackToSelection?.invoke()
                    }
                } else {
                    null
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            RoutineProgressHeader(state = state)

            Text(
                text = if (state.isFinished) "운동 완료!" else "세트 ${state.currentSet} / ${state.totalSets}",
                style = MaterialTheme.typography.headlineMedium,
                color = if (state.isFinished) accent else MaterialTheme.colorScheme.onBackground
            )

            TimerDial(state = state, accentColor = accent)

            when {
                state.isFinished -> FinishedSection(state = state, onReset = viewModel::reset)
                else -> ControlButtons(
                    state = state,
                    onToggle = viewModel::toggleTimer,
                    onComplete = viewModel::completeSet,
                    onStop = viewModel::stopAndSaveForLater,
                    onAbandon = viewModel::abandonWorkout,
                    onReset = viewModel::reset
                )
            }

            if (state.isPausedForLater && !state.isFinished) {
                Text(
                    text = "중지한 위치가 저장됐어요. 시작을 누르면 이어서 진행합니다.",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (!state.isRunning && !state.isRestMode && !state.isFinished) {
                SettingsSection(state = state, viewModel = viewModel)
            }
        }
    }
}

@Composable
private fun WeightInputDialog(
    initialWeightKg: Double?,
    onSave: (Double) -> Unit
) {
    var text by remember(initialWeightKg) {
        mutableStateOf(initialWeightKg?.let { "%.1f".format(it) } ?: "")
    }
    val weight = text.toDoubleOrNull()

    AlertDialog(
        onDismissRequest = {},
        title = { Text("몸무게 입력") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "칼로리 계산에 사용할 몸무게를 입력해주세요.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("몸무게 (kg)") },
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    )
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { weight?.let(onSave) },
                enabled = weight != null && weight > 0.0
            ) { Text("기록 저장") }
        }
    )
}

@Composable
private fun RoutineProgressHeader(state: TimerState) {
    val current = state.currentRoutineExercise()
    val next = state.nextRoutineExercise()

    if (current == null) {
        Text(
            text = state.routineName ?: "자유 운동",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        return
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = current.exercise.exerciseName,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "${current.exerciseSet} / ${current.exercise.customSets}세트 · ${current.exercise.customReps}회",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.fittColors.accent
        )
        Text(
            text = next?.let { "다음: ${it.exerciseName}" } ?: "마지막 운동",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun TimerDial(state: TimerState, accentColor: androidx.compose.ui.graphics.Color) {
    Box(contentAlignment = Alignment.Center) {
        if (state.isRestMode) {
            val progress by animateFloatAsState(
                targetValue = state.remainingRestSeconds.toFloat() / state.restDurationSeconds.toFloat(),
                animationSpec = tween(500),
                label = "restProgress"
            )
            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.size(220.dp),
                strokeWidth = 10.dp,
                strokeCap = StrokeCap.Round,
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "휴식", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    text = state.remainingRestSeconds.toTimeString(),
                    fontSize = 52.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        } else {
            CircularProgressIndicator(
                progress = { if (state.isRunning || state.elapsedSeconds > 0) 1f else 0f },
                modifier = Modifier.size(220.dp),
                strokeWidth = 10.dp,
                strokeCap = StrokeCap.Round,
                color = accentColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = if (state.isRunning) "운동 중" else "준비", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    text = state.elapsedSeconds.toTimeString(),
                    fontSize = 52.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

@Composable
private fun ControlButtons(
    state: TimerState,
    onToggle: () -> Unit,
    onComplete: () -> Unit,
    onStop: () -> Unit,
    onAbandon: () -> Unit,
    onReset: () -> Unit
) {
    val accent = MaterialTheme.fittColors.accent
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilledTonalIconButton(onClick = onReset, modifier = Modifier.size(48.dp)) {
            Icon(Icons.Filled.Refresh, contentDescription = "초기화")
        }
        FilledIconButton(
            onClick = onToggle,
            modifier = Modifier.size(72.dp),
            colors = IconButtonDefaults.filledIconButtonColors(containerColor = accent)
        ) {
            Icon(
                imageVector = if (state.isRunning) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                contentDescription = if (state.isRunning) "일시정지" else "시작",
                modifier = Modifier.size(36.dp),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
        FilledTonalIconButton(
            onClick = onComplete,
            modifier = Modifier.size(48.dp),
            enabled = state.elapsedSeconds > 0 || state.isRunning
        ) {
            Icon(Icons.Filled.SkipNext, contentDescription = "세트 완료")
        }
        FilledTonalIconButton(
            onClick = onStop,
            modifier = Modifier.size(48.dp),
            enabled = state.routineId != null && (state.elapsedSeconds > 0 || state.totalElapsedSeconds > 0 || state.isRunning)
        ) {
            Icon(Icons.Filled.Stop, contentDescription = "중지하고 이어하기 저장")
        }
        FilledTonalIconButton(
            onClick = onAbandon,
            modifier = Modifier.size(48.dp),
            enabled = state.elapsedSeconds > 0 || state.totalElapsedSeconds > 0 || state.isRunning || state.isPausedForLater
        ) {
            Icon(Icons.Filled.Delete, contentDescription = "완전 중단")
        }
    }
}

@Composable
private fun FinishedSection(state: TimerState, onReset: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = when {
                state.routineId == null -> "운동이 완료됐어요."
                state.isLogSaved -> "기록에 저장됐어요."
                else -> "기록 저장 중..."
            },
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        FilledTonalIconButton(onClick = onReset, modifier = Modifier.size(56.dp)) {
            Icon(Icons.Filled.Refresh, contentDescription = "다시 시작")
        }
    }
}

@Composable
private fun SettingsSection(state: TimerState, viewModel: TimerViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (state.routineId == null) {
            Text(text = "총 세트 수", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                setOptions.forEach { count ->
                    FilterChip(
                        selected = state.totalSets == count,
                        onClick = { viewModel.setTotalSets(count) },
                        label = { Text("${count}세트") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.fittColors.accent,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }
        }

        Text(text = "휴식 시간", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            restOptions.forEach { (seconds, label) ->
                FilterChip(
                    selected = state.restDurationSeconds == seconds,
                    onClick = { viewModel.setRestDuration(seconds) },
                    label = { Text(label) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.fittColors.accent,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }
    }
}

private fun Long.toTimeString(): String {
    val m = this / 60
    val s = this % 60
    return "%02d:%02d".format(m, s)
}

private data class CurrentRoutineExercise(
    val exercise: RoutineExercise,
    val exerciseSet: Int
)

private fun TimerState.currentRoutineExercise(): CurrentRoutineExercise? {
    var consumedSets = 0
    routineExercises.forEach { exercise ->
        val start = consumedSets + 1
        val end = consumedSets + exercise.customSets
        if (currentSet in start..end) {
            return CurrentRoutineExercise(
                exercise = exercise,
                exerciseSet = currentSet - consumedSets
            )
        }
        consumedSets = end
    }
    return null
}

private fun TimerState.nextRoutineExercise(): RoutineExercise? {
    val current = currentRoutineExercise()?.exercise ?: return null
    val currentIndex = routineExercises.indexOfFirst { it.exerciseId == current.exerciseId }
    if (currentIndex < 0) return null
    return routineExercises.getOrNull(currentIndex + 1)
}
