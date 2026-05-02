package com.gw.fitt.presentation.log

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gw.fitt.domain.model.WorkoutLog
import com.gw.fitt.ui.component.FittCard
import com.gw.fitt.ui.component.FittTopBar
import com.gw.fitt.ui.theme.fittColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LogScreen(viewModel: LogViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()

    Scaffold(topBar = { FittTopBar(title = "운동 기록") }) { innerPadding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                androidx.compose.material3.CircularProgressIndicator(color = MaterialTheme.fittColors.accent)
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(Modifier.height(8.dp))
                WeeklySummarySection(
                    workoutCount = state.weeklyStats.workoutCount,
                    totalMinutes = state.weeklyStats.totalMinutes,
                    totalCalories = state.weeklyStats.totalCalories
                )
            }

            item {
                WeeklyBarChart(dailyMinutes = state.dailyMinutes)
            }

            if (state.logs.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "아직 운동 기록이 없어요.\n타이머로 첫 운동을 기록해 보세요!",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                item {
                    Text(
                        text = "전체 기록",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                items(state.logs, key = { it.id }) { log ->
                    WorkoutLogItem(log = log)
                }
            }

            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun WeeklySummarySection(workoutCount: Int, totalMinutes: Int, totalCalories: Int) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(text = "이번 주 요약", style = MaterialTheme.typography.headlineMedium)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SummaryChip(label = "운동",   value = "${workoutCount}회",      modifier = Modifier.weight(1f))
            SummaryChip(label = "시간",   value = "${totalMinutes}분",       modifier = Modifier.weight(1f))
            SummaryChip(label = "칼로리", value = "${totalCalories}kcal",   modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun SummaryChip(label: String, value: String, modifier: Modifier = Modifier) {
    FittCard(modifier = modifier) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.fittColors.accent)
            Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun WeeklyBarChart(dailyMinutes: List<Float>) {
    val accent = MaterialTheme.fittColors.accent
    val maxVal = remember(dailyMinutes) { dailyMinutes.maxOrNull()?.coerceAtLeast(1f) ?: 1f }
    val dayLabels = listOf("6일전", "5일전", "4일전", "3일전", "2일전", "어제", "오늘")

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = "일별 운동 시간 (분)", style = MaterialTheme.typography.headlineMedium)
        FittCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    dailyMinutes.forEachIndexed { i, minutes ->
                        val barHeightFraction = (minutes / maxVal).coerceIn(0f, 1f)
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom,
                            modifier = Modifier.weight(1f).padding(horizontal = 3.dp)
                        ) {
                            if (minutes > 0f) {
                                Text(
                                    text = "${minutes.toInt()}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(Modifier.height(2.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height((100 * barHeightFraction).coerceAtLeast(4f).dp)
                                    .clip(MaterialTheme.shapes.small)
                                    .background(if (i == 6) accent else accent.copy(alpha = 0.5f))
                            )
                        }
                    }
                }
                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    dayLabels.forEach { label ->
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WorkoutLogItem(log: WorkoutLog) {
    val dateStr = remember(log.startedAt) {
        SimpleDateFormat("yyyy.MM.dd  HH:mm", Locale.KOREAN).format(Date(log.startedAt))
    }
    FittCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = log.routineName, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                Text(text = dateStr, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = "${log.durationMinutes}분", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                Text(text = "${log.totalCalories} kcal", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
