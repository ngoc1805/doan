package com.example.models

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class ServiceAppointment (
    val id : Int,
    val appointmentId: Int,
    val serviceRoomId: Int,
    val status: String,
    val examDate: LocalDate
)