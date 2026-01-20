package com.example.models

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Notification(
    val id: Int,
    val userId: Int,
    val content: String,
    val isSeen: Boolean = false,
    val isReceived: Boolean = false,
    val createdAt: LocalDateTime,
    val path: String
)
