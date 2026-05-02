package com.gw.fitt.domain.model

data class Routine(
    val id: Int,
    val name: String,
    val level: String,
    val estimatedMinutes: Int,
    val createdAt: Long
)
