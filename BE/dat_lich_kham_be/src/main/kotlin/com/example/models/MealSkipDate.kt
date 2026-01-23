package com.example.models

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class MealSkipDate(
    val id: Int,
    val cycleId: Int,
    val inpatientId: Int,
    val skipDate: LocalDate,
    val createdAt: Instant
)