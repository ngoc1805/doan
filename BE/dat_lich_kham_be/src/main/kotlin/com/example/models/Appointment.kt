package com.example.models

import kotlinx.serialization.Serializable
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.Instant

@Serializable
data class Appointment(
    val id: Int,
    val userId: Int,
    val doctorId: Int,
    val examDate: LocalDate,
    val examTime: LocalTime,
    val status: String,
    val createdAt: Instant
)