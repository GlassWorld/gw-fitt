package com.gw.fitt.presentation.routine

import com.gw.fitt.domain.model.Routine

data class RoutineState(
    val isLoading: Boolean = true,
    val routines: List<Routine> = emptyList(),
    val showCreateDialog: Boolean = false,
    val error: String? = null
)
