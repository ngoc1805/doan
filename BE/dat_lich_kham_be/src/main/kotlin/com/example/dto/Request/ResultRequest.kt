package com.example.dto.Request

import kotlinx.serialization.Serializable

@Serializable
data class ResultRequest(
    val appointmentId: Int,
    val comment: String
)
