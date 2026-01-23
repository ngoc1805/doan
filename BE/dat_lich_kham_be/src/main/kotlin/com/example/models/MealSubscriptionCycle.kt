package com.example.models

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class MealSubscriptionCycle(
    val id: Int,
    val inpatientId: Int,
    val cycleNumber: Int,
    val weekStartDate: LocalDate,
    val weekEndDate: LocalDate,
    val registrationDate: LocalDate,
    val status: String,
    val daysInCycle: Int,
    val expectedMealDays: Int,
    val actualMealDays: Int,
    val actualSkipDays: Int,
    val createdAt: Instant,
    val updatedAt: Instant
)