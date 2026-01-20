package com.example.dto.Request

import kotlinx.serialization.Serializable
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

@Serializable
data class FreeTimeRequest(
    val doctorId: Int,
    val date: LocalDate,
    val slots: List<LocalTime>
)