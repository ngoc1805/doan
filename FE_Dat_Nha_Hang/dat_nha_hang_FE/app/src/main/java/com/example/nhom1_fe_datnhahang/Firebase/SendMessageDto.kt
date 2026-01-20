package com.example.nhom1_fe_datnhahang.Firebase

data class SendMessageDto(
    val to: String?,
    val notification: NotificationBody
)
data class NotificationBody(
    val title: String,
    val body: String
)
