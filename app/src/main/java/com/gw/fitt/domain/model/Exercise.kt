package com.gw.fitt.domain.model

data class Exercise(
    val id: Int,
    val name: String,
    val category: String,
    val defaultSets: Int,
    val defaultReps: Int,
    val durationSec: Int
)
