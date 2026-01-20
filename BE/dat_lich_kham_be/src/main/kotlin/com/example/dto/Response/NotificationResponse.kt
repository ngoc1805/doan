package com.example.dto.Response

import com.example.models.Notification
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class NotificationItem(
    val id: Int,
    val content: String,
    val isSeen: Boolean = false,
    val createdAt: LocalDateTime,
    val path: String
)

@Serializable
data class NotificationListResponse(
    val notifications: List<NotificationItem>
)