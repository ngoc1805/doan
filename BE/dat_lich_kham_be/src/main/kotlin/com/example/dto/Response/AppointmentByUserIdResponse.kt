package com.example.dto.Response


import com.example.models.Appointment
import com.example.models.Notification
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable

@Serializable
data class AppointmentItem(
    val id: Int,
    val doctorId: Int,
    val doctorName: String,
    val doctorCode: String,
    val department: String,
    val examPrice: Int,
    val examDate: LocalDate,
    val examTime: LocalTime,
    val status: String,

    )

@Serializable
data class AppointmentByUserIdListResponse(
    val appointments: List<AppointmentItem>
)