package com.gw.fitt.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gw.fitt.domain.model.Routine
import com.gw.fitt.domain.model.WeeklyStats
import com.gw.fitt.ui.component.Difficulty
import com.gw.fitt.ui.component.FittBadge
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
        WeeklyStatsSection(stats = state.weeklyStats)
        if (state.recentRoutines.isNotEmpty()) {
            RecentRoutinesSection(routines = state.recentRoutines)
        } else {
            EmptyRoutineHint()
        }
    }
}

@Composable
private fun HomeHeader() {
    val today = SimpleDateFormat("M월 d일 (E)", Locale.KOREAN).format(Date())
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = "안녕하세요!",
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
private fun WeeklyStatsSection(stats: WeeklyStats) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(text = "이번 주 운동", style = MaterialTheme.typography.headlineMedium)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatCard(label = "운동 횟수",   value = "${stats.workoutCount}회",      modifier = Modifier.weight(1f))
            StatCard(label = "운동 시간",   value = "${stats.totalMinutes}분",       modifier = Modifier.weight(1f))
            StatCard(label = "소모 칼로리", value = "${stats.totalCalories}kcal",   modifier = Modifier.weight(1f))
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
private fun RecentRoutinesSection(routines: List<Routine>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(text = "최근 루틴", style = MaterialTheme.typography.headlineMedium)
        routines.forEach { RoutineRow(routine = it) }
    }
}

@Composable
private fun RoutineRow(routine: Routine) {
    val difficulty = routine.level.toDifficulty()
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
                    text = routine.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "⏱ ${routine.estimatedMinutes}분",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            FittBadge(difficulty = difficulty)
        }
    }
}

@Composable
private fun EmptyRoutineHint() {
    FittCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "아직 루틴이 없어요", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
            Text(
                text = "루틴 탭에서 첫 번째 루틴을 만들어 보세요!",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun String.toDifficulty() = when (this) {
    "중급" -> Difficulty.INTERMEDIATE
    "고급" -> Difficulty.ADVANCED
    else  -> Difficulty.BEGINNER
}
