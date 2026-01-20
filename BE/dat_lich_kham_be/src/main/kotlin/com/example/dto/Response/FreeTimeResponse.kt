package com.example.dto.Response

import kotlinx.serialization.Serializable
import kotlinx.datetime.LocalTime

@Serializable
data class FreeTimeResponse(
    val freeSlots: List<LocalTime>
)