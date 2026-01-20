package com.example.dat_lich_kham_fe.data.model

import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalTime

data class FreeTimeRequest(
    val doctorId: Int,
    val date: String,
    val slots: List<String>
)

data class FreeTimeResponse(
    val freeSlots: List<String>
)

data class AppointmentRequest(
    val userId: Int,
    val doctorId: Int,
    val examDate: String,
    val examTime: String,
    val status: String,
)

data class AppointmentItem(
    val id: Int,
    val doctorId: Int,
    val doctorName: String,
    val doctorCode: String,
    val department: String,
    val examPrice: Int,
    val examDate: String,
    val examTime: String,
    val status: String,

    )

data class AppointmentByUserIdListResponse(
    val appointments: List<AppointmentItem>
)

data class UpdateAppointmentSatatusRequest (
    val appointmentId: Int,
    val status : String
)
