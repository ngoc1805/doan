package com.example.dto.Request

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable

@Serializable
    data class AppointmentRequest(
        val userId: Int,
        val doctorId: Int,
        val examDate: LocalDate,
        val examTime: LocalTime,
        val status: String,
    )
