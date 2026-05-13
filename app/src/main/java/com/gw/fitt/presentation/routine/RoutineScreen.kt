package com.gw.fitt.presentation.routine

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gw.fitt.domain.model.Exercise
import com.gw.fitt.domain.model.RoutineExercise
import com.gw.fitt.ui.component.FittTopBar
import com.gw.fitt.ui.theme.fittColors

@Composable
fun RoutineScreen(
    onStartSelectedWorkout: (List<RoutineExercise>) -> Unit = {},
    viewModel: RoutineViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val selectedOrder = remember { mutableStateListOf<Int>() }
    val sets = remember { mutableStateMapOf<Int, Int>() }
    val reps = remember { mutableStateMapOf<Int, Int>() }

    LaunchedEffect(state.homeExercises) {
        state.homeExercises.forEach { exercise ->
            sets.putIfAbsent(exercise.id, exercise.defaultSets)
            reps.putIfAbsent(exercise.id, exercise.defaultReps)
        }
        if (selectedOrder.isEmpty()) {
            state.homeExercises
                .filter { it.name in listOf("스쿼트", "팔굽혀펴기", "플랭크") }
                .forEach { selectedOrder.add(it.id) }
        }
    }

    val selectedExercises = selectedOrder.toRoutineExercises(state.homeExercises, sets, reps)
    val groupedExercises = state.homeExercises.groupBy { it.category }

    Scaffold(topBar = { FittTopBar(title = "선택 운동") }) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "운동 선택",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "오늘 할 운동을 고르고 세트와 횟수를 맞춘 뒤 바로 시작하세요.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Button(
                onClick = { onStartSelectedWorkout(selectedExercises) },
                enabled = selectedExercises.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("선택 운동 시작")
            }

            if (state.homeExercises.isEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = MaterialTheme.fittColors.accent
                )
            } else {
                groupedExercises.forEach { (category, exercises) ->
                    Text(
                        text = category,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    exercises.forEach { exercise ->
                        ExerciseSelectRow(
                            exercise = exercise,
                            selected = exercise.id in selectedOrder,
                            sets = sets[exercise.id] ?: exercise.defaultSets,
                            reps = reps[exercise.id] ?: exercise.defaultReps,
                            onSelectedChange = { checked ->
                                if (checked && exercise.id !in selectedOrder) selectedOrder.add(exercise.id)
                                if (!checked) selectedOrder.remove(exercise.id)
                            },
                            onSetsChange = { value -> sets[exercise.id] = value },
                            onRepsChange = { value -> reps[exercise.id] = value }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ExerciseSelectRow(
    exercise: Exercise,
    selected: Boolean,
    sets: Int,
    reps: Int,
    onSelectedChange: (Boolean) -> Unit,
    onSetsChange: (Int) -> Unit,
    onRepsChange: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 72.dp)
            .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = selected, onCheckedChange = onSelectedChange)
            Column {
                Text(text = exercise.name, style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = if (exercise.durationSec > 0) "${exercise.durationSec}초 기준" else "반복 운동",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        if (selected) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Stepper("세트", sets, 1, onSetsChange, Modifier.weight(1f))
                Stepper("횟수", reps, 1, onRepsChange, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun Stepper(
    label: String,
    value: Int,
    min: Int,
    onChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "$label $value", style = MaterialTheme.typography.labelMedium)
        Row {
            FilledTonalIconButton(
                onClick = { onChange((value - 1).coerceAtLeast(min)) },
                modifier = Modifier.heightIn(min = 40.dp)
            ) {
                Icon(Icons.Filled.Remove, contentDescription = "$label 줄이기")
            }
            FilledTonalIconButton(
                onClick = { onChange(value + 1) },
                modifier = Modifier.heightIn(min = 40.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "$label 늘리기")
            }
        }
    }
}

private fun List<Int>.toRoutineExercises(
    exercises: List<Exercise>,
    sets: Map<Int, Int>,
    reps: Map<Int, Int>
): List<RoutineExercise> {
    val exercisesById = exercises.associateBy { it.id }
    return mapIndexedNotNull { index, exerciseId ->
        exercisesById[exerciseId]?.let { exercise ->
            RoutineExercise(
                routineId = 0,
                exerciseId = exercise.id,
                exerciseName = exercise.name,
                category = exercise.category,
                orderIndex = index,
                customSets = sets[exercise.id] ?: exercise.defaultSets,
                customReps = reps[exercise.id] ?: exercise.defaultReps,
                durationSec = exercise.durationSec,
                met = exercise.met
            )
        }
    }
}
