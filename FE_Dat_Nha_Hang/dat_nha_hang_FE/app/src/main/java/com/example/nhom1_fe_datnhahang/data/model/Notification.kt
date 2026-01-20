package com.example.nhom1_fe_datnhahang.data.model

import kotlinx.serialization.Serializable

data class NotificationRequest(
    val userId: Int,
    val content: String,
    val path: String
)


data class NotificationItem(
    val id: Int,
    val content: String,
    val isSeen: Boolean = false,
    val createdAt: String,
    val path: String
)

data class NotificationListResponse(
    val notifications: List<NotificationItem>
)
