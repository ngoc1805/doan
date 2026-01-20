package com.example.dto.Request

import kotlinx.serialization.Serializable

@Serializable
data class UpdateAppointmentSatatusRequest (
    val appointmentId: Int,
    val status : String
)