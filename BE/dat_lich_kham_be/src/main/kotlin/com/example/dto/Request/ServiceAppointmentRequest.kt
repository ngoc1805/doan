package com.example.dto.Request

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class ServiceAppointmentRequest(
    val appointmentId: Int,
    val serviceRoomId: Int,
    val status: String,
    val examDate: LocalDate
)
