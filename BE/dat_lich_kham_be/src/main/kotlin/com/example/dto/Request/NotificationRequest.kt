package com.example.dto.Request

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class NotificationRequest(
    val userId: Int,
    val content: String,
    val path: String
)
