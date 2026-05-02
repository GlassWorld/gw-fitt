package com.gw.fitt.presentation.timer

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gw.fitt.ui.component.FittTopBar
import com.gw.fitt.ui.theme.fittColors

private val setOptions  = listOf(1, 2, 3, 4, 5)
private val restOptions = listOf(30L to "30초", 60L to "1분", 90L to "1분 30초", 120L to "2분")

@Composable
fun TimerScreen(viewModel: TimerViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    val accent = MaterialTheme.fittColors.accent

    Scaffold(topBar = { FittTopBar(title = "타이머") }) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            // 세트 표시
            Text(
                text = if (state.isFinished) "운동 완료!" else "세트 ${state.currentSet} / ${state.totalSets}",
                style = MaterialTheme.typography.headlineMedium,
                color = if (state.isFinished) accent else MaterialTheme.colorScheme.onBackground
            )

            // 원형 타이머
            TimerDial(state = state, accentColor = accent)

            // 제어 버튼
            if (!state.isFinished) {
                ControlButtons(
                    state = state,
                    onToggle = viewModel::toggleTimer,
                    onComplete = viewModel::completeSet,
                    onReset = viewModel::reset
                )
            } else {
                FilledTonalIconButton(onClick = viewModel::reset, modifier = Modifier.size(56.dp)) {
                    Icon(Icons.Filled.Refresh, contentDescription = "다시 시작")
                }
            }

            // 설정 영역 (실행 중이 아닐 때만 변경 가능)
            if (!state.isRunning && !state.isRestMode && !state.isFinished) {
                SettingsSection(state = state, viewModel = viewModel)
            }
        }
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
    }
}

@Composable
private fun SettingsSection(state: TimerState, viewModel: TimerViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
