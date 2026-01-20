package com.example.dat_lich_kham_fe.Firebase

data class SendMessageDto(
    val to: String?,
    val notification: NotificationBody
)
data class NotificationBody(
    val title: String,
    val body: String
)
