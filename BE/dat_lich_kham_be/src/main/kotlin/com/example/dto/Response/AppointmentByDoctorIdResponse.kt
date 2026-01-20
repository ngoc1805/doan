package com.example.dto.Response

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable

@Serializable
data class AppointmentByDoctorIdItem(
    val id: Int,
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
data class AppointmentByDoctorIdListResponse(
    val appointments: List<AppointmentByDoctorIdItem>
)