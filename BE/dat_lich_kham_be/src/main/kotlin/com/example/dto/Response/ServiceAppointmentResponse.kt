package com.example.dto.Response

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable

@Serializable
data class ServiceAppointmentItem(
    val id: Int,
    val appointmentId: Int,
    val userId: Int,
    val userName: String,
    val gender: String,
    val birthDate: LocalDate,
    val homeTown: String,
    val cccd: String,
    val examDate: LocalDate,
    val examTime: LocalTime,
    val status : String,
    val fmctoken : String
)

@Serializable
data class ListServiceAppointment(
    val serviceappointments: List<ServiceAppointmentItem>
)