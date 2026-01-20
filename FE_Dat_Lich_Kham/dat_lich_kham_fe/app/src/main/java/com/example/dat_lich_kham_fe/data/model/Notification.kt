package com.example.dat_lich_kham_fe.data.model

import kotlinx.serialization.Serializable
@Serializable
data class NotificationRequest(
    val userId: Int,
    val content: String,
    val path: String
)

@Serializable
data class NotificationItem(
    val id: Int,
    val content: String,
    val isSeen: Boolean = false,
    val createdAt: String,
    val path: String
)
@Serializable
data class NotificationListResponse(
    val notifications: List<NotificationItem>
)
