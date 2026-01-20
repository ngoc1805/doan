package com.example.dto.Request

import kotlinx.serialization.Serializable
import kotlinx.datetime.LocalDate

@Serializable
data class UpdateServiceAppointmentStatusRequest(
    val serviceAppointmentId: Int,
    val status: String,
)
