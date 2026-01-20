package com.example.nhom1_fe_datnhahang.Firebase

data class ChatState(
    val isEnteringToken: Boolean = true,
    val remoteToken: String = "",
    val messageText: String = ""
)

