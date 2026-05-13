package com.gw.fitt.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gw.fitt.domain.model.WeeklyStats
import com.gw.fitt.domain.model.WorkoutLog
import com.gw.fitt.ui.component.FittCard
import com.gw.fitt.ui.theme.fittColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()

    if (state.isLoading) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(color = MaterialTheme.fittColors.accent)
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        HomeHeader()
        WeightCard(weightKg = state.weightKg, onSave = viewModel::saveWeight)
        WeeklyStatsSection(stats = state.weeklyStats)
        if (state.recentWorkoutLogs.isNotEmpty()) {
            RecentWorkoutSection(logs = state.recentWorkoutLogs)
        } else {
            EmptyWorkoutHint()
        }
    }
}

@Composable
private fun HomeHeader() {
    val today = SimpleDateFormat("M월 d일 (E)", Locale.KOREAN).format(Date())
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = "오늘도 움직여볼까요?",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = today,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun WeightCard(
    weightKg: Double?,
    onSave: (Double) -> Unit
) {
    var text by remember { mutableStateOf("") }
    LaunchedEffect(weightKg) {
        text = weightKg?.let { "%.1f".format(it) } ?: ""
    }
    val parsed = text.toDoubleOrNull()

    FittCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "내 체중",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("kg") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f)
                )
                TextButton(
                    onClick = { parsed?.let(onSave) },
                    enabled = parsed != null && parsed > 0.0
                ) {
                    Text("저장")
                }
            }
            Text(
                text = "운동 완료 시 칼로리 계산 기본값으로 사용됩니다.",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun WeeklyStatsSection(stats: WeeklyStats) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(text = "이번 주 운동", style = MaterialTheme.typography.headlineMedium)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatCard(label = "운동 횟수", value = "${stats.workoutCount}회", modifier = Modifier.weight(1f))
            StatCard(label = "운동 시간", value = "${stats.totalMinutes}분", modifier = Modifier.weight(1f))
            StatCard(label = "소모 칼로리", value = "${stats.totalCalories}kcal", modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    FittCard(modifier = modifier) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.fittColors.accent
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun RecentWorkoutSection(logs: List<WorkoutLog>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(text = "최근 운동", style = MaterialTheme.typography.headlineMedium)
        logs.forEach { WorkoutLogRow(log = it) }
    }
}

@Composable
private fun WorkoutLogRow(log: WorkoutLog) {
    val dateStr = remember(log.startedAt) {
        SimpleDateFormat("M월 d일 HH:mm", Locale.KOREAN).format(Date(log.startedAt))
    }

    FittCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = log.routineName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = dateStr,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "${log.durationMinutes}분",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${log.totalCalories} kcal",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun EmptyWorkoutHint() {
    FittCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "아직 운동 기록이 없어요", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
            Text(
                text = "선택운동이나 타이머로 첫 운동을 완료해보세요.",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
