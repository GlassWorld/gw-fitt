package com.gw.fitt.presentation.routine

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DragHandle
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gw.fitt.domain.model.Exercise
import com.gw.fitt.domain.model.Routine
import com.gw.fitt.domain.model.RoutineExercise
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
                    onDelete = viewModel::requestDeleteRoutine
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
            onSave = viewModel::updateRoutineExercises,
            onDismiss = viewModel::hideRoutineDetail
        )
    }

    state.routinePendingDelete?.let { routine ->
        DeleteRoutineDialog(
            routine = routine,
            onConfirm = viewModel::confirmDeleteRoutine,
            onDismiss = viewModel::cancelDeleteRoutine
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
    val selectedInputs = selectedOrder.toCreateInputs(exercisesById, sets, reps)

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
                        onSelectedChange = { checked ->
                            if (checked && exercise.id !in selectedOrder) selectedOrder.add(exercise.id)
                            if (!checked) selectedOrder.remove(exercise.id)
                        },
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
    onSelectedChange: (Boolean) -> Unit,
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
                    Stepper("세트", sets, 1, onSetsChange, Modifier.weight(1f))
                    Stepper("횟수", reps, 1, onRepsChange, Modifier.weight(1f))
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
    onSave: (Int, List<RoutineExerciseInput>) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val exercisesById = detail.exercises.associateBy { it.exerciseId }
    val selectedOrder = remember(detail.routine.id) {
        mutableStateListOf<Int>().apply {
            addAll(detail.exercises.sortedBy { it.orderIndex }.map { it.exerciseId })
        }
    }
    val sets = remember(detail.routine.id) { mutableStateMapOf<Int, Int>() }
    val reps = remember(detail.routine.id) { mutableStateMapOf<Int, Int>() }

    LaunchedEffect(detail) {
        detail.exercises.forEach { exercise ->
            sets[exercise.exerciseId] = exercise.customSets
            reps[exercise.exerciseId] = exercise.customReps
        }
    }

    val orderedExercises = selectedOrder.mapNotNull { exercisesById[it] }
    val selectedInputs = selectedOrder.toEditInputs(exercisesById, sets, reps)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(detail.routine.name) },
        text = {
            Column(
                modifier = Modifier
                    .heightIn(max = 560.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "${detail.routine.estimatedMinutes}분",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Button(
                    onClick = onStart,
                    enabled = selectedInputs.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
                ) { Text("운동 시작") }
                Text(
                    text = "세트, 횟수, 순서 편집",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                orderedExercises.forEach { exercise ->
                    RoutineExerciseEditRow(
                        exercise = exercise,
                        sets = sets[exercise.exerciseId] ?: exercise.customSets,
                        reps = reps[exercise.exerciseId] ?: exercise.customReps,
                        onMoveUp = { selectedOrder.move(exercise.exerciseId, -1) },
                        onMoveDown = { selectedOrder.move(exercise.exerciseId, 1) },
                        onSetsChange = { value -> sets[exercise.exerciseId] = value },
                        onRepsChange = { value -> reps[exercise.exerciseId] = value }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(detail.routine.id, selectedInputs)
                    Toast.makeText(context, "변경사항을 저장했어요.", Toast.LENGTH_SHORT).show()
                },
                enabled = selectedInputs.isNotEmpty()
            ) { Text("변경 저장") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("닫기") }
        }
    )
}

@Composable
private fun DeleteRoutineDialog(
    routine: Routine,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("루틴 삭제") },
        text = {
            Text(
                text = "'${routine.name}' 루틴을 삭제할까요? 삭제한 루틴은 되돌릴 수 없습니다.",
                style = MaterialTheme.typography.bodyLarge
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("삭제", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("취소") }
        }
    )
}

@Composable
private fun RoutineExerciseEditRow(
    exercise: RoutineExercise,
    sets: Int,
    reps: Int,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onSetsChange: (Int) -> Unit,
    onRepsChange: (Int) -> Unit
) {
    var dragOffset by remember(exercise.exerciseId) { mutableFloatStateOf(0f) }
    val dragThresholdPx = 72f

    FittCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .pointerInput(exercise.exerciseId) {
                            detectVerticalDragGestures(
                                onDragStart = { dragOffset = 0f },
                                onVerticalDrag = { _, dragAmount ->
                                    dragOffset += dragAmount
                                    when {
                                        dragOffset <= -dragThresholdPx -> {
                                            onMoveUp()
                                            dragOffset = 0f
                                        }
                                        dragOffset >= dragThresholdPx -> {
                                            onMoveDown()
                                            dragOffset = 0f
                                        }
                                    }
                                },
                                onDragEnd = { dragOffset = 0f },
                                onDragCancel = { dragOffset = 0f }
                            )
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.DragHandle,
                        contentDescription = "드래그로 순서 변경",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = exercise.exerciseName,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                Text(
                    text = if (exercise.durationSec > 0) "${exercise.durationSec}초 기준" else "반복 운동",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
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

private fun List<Int>.toCreateInputs(
    exercisesById: Map<Int, Exercise>,
    sets: Map<Int, Int>,
    reps: Map<Int, Int>
): List<RoutineExerciseInput> = mapNotNull { exerciseId ->
    exercisesById[exerciseId]?.let { exercise ->
        RoutineExerciseInput(
            exerciseId = exercise.id,
            sets = sets[exercise.id] ?: exercise.defaultSets,
            reps = reps[exercise.id] ?: exercise.defaultReps
        )
    }
}

private fun List<Int>.toEditInputs(
    exercisesById: Map<Int, RoutineExercise>,
    sets: Map<Int, Int>,
    reps: Map<Int, Int>
): List<RoutineExerciseInput> = mapNotNull { exerciseId ->
    exercisesById[exerciseId]?.let { exercise ->
        RoutineExerciseInput(
            exerciseId = exercise.exerciseId,
            sets = sets[exercise.exerciseId] ?: exercise.customSets,
            reps = reps[exercise.exerciseId] ?: exercise.customReps
        )
    }
}

private fun MutableList<Int>.move(item: Int, offset: Int) {
    val from = indexOf(item)
    val to = from + offset
    if (from < 0 || to !in indices) return
    removeAt(from)
    add(to, item)
}
