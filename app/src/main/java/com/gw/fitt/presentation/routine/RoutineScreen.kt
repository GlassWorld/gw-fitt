package com.gw.fitt.presentation.routine

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gw.fitt.domain.model.Exercise
import com.gw.fitt.domain.model.Routine
import com.gw.fitt.domain.model.RoutineExerciseInput
import com.gw.fitt.domain.model.RoutineWithExercises
import com.gw.fitt.ui.component.FittCard
import com.gw.fitt.ui.component.FittTopBar
import com.gw.fitt.ui.theme.fittColors

@Composable
fun RoutineScreen(
    onStartRoutine: (RoutineWithExercises) -> Unit = {},
    viewModel: RoutineViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = { FittTopBar(title = "루틴") },
        floatingActionButton = {
            FloatingActionButton(
                onClick = viewModel::showCreateDialog,
                containerColor = MaterialTheme.fittColors.accent,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "루틴 추가")
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                state.isLoading -> CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.fittColors.accent
                )

                state.routines.isEmpty() -> EmptyRoutineMessage(
                    modifier = Modifier.align(Alignment.Center)
                )

                else -> RoutineList(
                    routines = state.routines,
                    onSelect = viewModel::showRoutineDetail,
                    onDelete = viewModel::deleteRoutine
                )
            }
        }
    }

    if (state.showCreateDialog) {
        CreateRoutineDialog(
            exercises = state.homeExercises,
            onConfirm = viewModel::createRoutine,
            onDismiss = viewModel::hideCreateDialog
        )
    }

    state.selectedRoutine?.let { detail ->
        RoutineDetailDialog(
            detail = detail,
            onStart = { onStartRoutine(detail) },
            onDismiss = viewModel::hideRoutineDetail
        )
    }
}

@Composable
private fun RoutineList(
    routines: List<Routine>,
    onSelect: (Int) -> Unit,
    onDelete: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(routines, key = { it.id }) { routine ->
            RoutineItem(
                routine = routine,
                onSelect = { onSelect(routine.id) },
                onDelete = { onDelete(routine.id) }
            )
        }
    }
}

@Composable
private fun RoutineItem(
    routine: Routine,
    onSelect: () -> Unit,
    onDelete: () -> Unit
) {
    FittCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = routine.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${routine.estimatedMinutes}분",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "삭제",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun EmptyRoutineMessage(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "루틴이 없어요", style = MaterialTheme.typography.headlineMedium)
        Text(
            text = "+ 버튼을 눌러 첫 맨몸운동 루틴을 만들어보세요.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun CreateRoutineDialog(
    exercises: List<Exercise>,
    onConfirm: (String, Int, List<RoutineExerciseInput>) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var estimatedMinutes by remember { mutableIntStateOf(30) }
    var minutesText by remember { mutableStateOf("30") }
    val selectedOrder = remember { mutableStateListOf<Int>() }
    val sets = remember { mutableStateMapOf<Int, Int>() }
    val reps = remember { mutableStateMapOf<Int, Int>() }
    val defaultSelectedNames = remember { setOf("스쿼트", "팔굽혀펴기", "윗몸일으키기") }

    LaunchedEffect(exercises) {
        exercises.forEach { exercise ->
            sets.putIfAbsent(exercise.id, exercise.defaultSets)
            reps.putIfAbsent(exercise.id, exercise.defaultReps)
            if (exercise.name in defaultSelectedNames && exercise.id !in selectedOrder) {
                selectedOrder.add(exercise.id)
            }
        }
    }

    val exercisesById = exercises.associateBy { it.id }
    val selectedInputs = selectedOrder.mapNotNull { exerciseId ->
        exercisesById[exerciseId]?.let { exercise ->
            RoutineExerciseInput(
                exerciseId = exercise.id,
                sets = sets[exercise.id] ?: exercise.defaultSets,
                reps = reps[exercise.id] ?: exercise.defaultReps
            )
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("맨몸운동 루틴 만들기") },
        text = {
            Column(
                modifier = Modifier
                    .heightIn(max = 560.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("루틴 이름") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = minutesText,
                    onValueChange = { text ->
                        minutesText = text
                        estimatedMinutes = text.toIntOrNull() ?: 0
                    },
                    label = { Text("예상 시간 (분)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "운동 구성",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )

                exercises.forEach { exercise ->
                    ExercisePickerRow(
                        exercise = exercise,
                        selected = exercise.id in selectedOrder,
                        sets = sets[exercise.id] ?: exercise.defaultSets,
                        reps = reps[exercise.id] ?: exercise.defaultReps,
                        canMoveUp = selectedOrder.indexOf(exercise.id) > 0,
                        canMoveDown = selectedOrder.indexOf(exercise.id) in 0 until selectedOrder.lastIndex,
                        onSelectedChange = { checked ->
                            if (checked && exercise.id !in selectedOrder) {
                                selectedOrder.add(exercise.id)
                            } else if (!checked) {
                                selectedOrder.remove(exercise.id)
                            }
                        },
                        onMoveUp = { selectedOrder.move(exercise.id, -1) },
                        onMoveDown = { selectedOrder.move(exercise.id, 1) },
                        onSetsChange = { value -> sets[exercise.id] = value },
                        onRepsChange = { value -> reps[exercise.id] = value }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(name, estimatedMinutes, selectedInputs) },
                enabled = name.isNotBlank() && selectedInputs.isNotEmpty()
            ) { Text("저장") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("취소") }
        }
    )
}

@Composable
private fun ExercisePickerRow(
    exercise: Exercise,
    selected: Boolean,
    sets: Int,
    reps: Int,
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    onSelectedChange: (Boolean) -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onSetsChange: (Int) -> Unit,
    onRepsChange: (Int) -> Unit
) {
    FittCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Checkbox(checked = selected, onCheckedChange = onSelectedChange)
                    Text(text = exercise.name, style = MaterialTheme.typography.bodyLarge)
                }
                Text(
                    text = if (exercise.durationSec > 0) "${exercise.durationSec}초 기준" else "반복 운동",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (selected) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Stepper(
                        label = "세트",
                        value = sets,
                        min = 1,
                        onChange = onSetsChange,
                        modifier = Modifier.weight(1f)
                    )
                    Stepper(
                        label = "횟수",
                        value = reps,
                        min = 1,
                        onChange = onRepsChange,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onMoveUp, enabled = canMoveUp) {
                        Icon(Icons.Filled.KeyboardArrowUp, contentDescription = "순서 올리기")
                    }
                    IconButton(onClick = onMoveDown, enabled = canMoveDown) {
                        Icon(Icons.Filled.KeyboardArrowDown, contentDescription = "순서 내리기")
                    }
                }
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
            IconButton(onClick = { onChange((value - 1).coerceAtLeast(min)) }) {
                Icon(Icons.Filled.Remove, contentDescription = "$label 줄이기")
            }
            IconButton(onClick = { onChange(value + 1) }) {
                Icon(Icons.Filled.Add, contentDescription = "$label 늘리기")
            }
        }
    }
}

@Composable
private fun RoutineDetailDialog(
    detail: RoutineWithExercises,
    onStart: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(detail.routine.name) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "${detail.routine.estimatedMinutes}분",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (detail.exercises.isEmpty()) {
                    Text("아직 운동이 들어있지 않아요.")
                } else {
                    detail.exercises.forEachIndexed { index, exercise ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${index + 1}. ${exercise.exerciseName}")
                            Spacer(Modifier.width(12.dp))
                            Text("${exercise.customSets}세트 x ${exercise.customReps}회")
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onStart,
                enabled = detail.exercises.isNotEmpty()
            ) { Text("운동 시작") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("닫기") }
        }
    )
}

private fun MutableList<Int>.move(item: Int, offset: Int) {
    val from = indexOf(item)
    val to = from + offset
    if (from < 0 || to !in indices) return
    removeAt(from)
    add(to, item)
}
