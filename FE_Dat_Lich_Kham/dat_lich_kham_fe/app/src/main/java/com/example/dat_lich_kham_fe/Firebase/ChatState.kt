package com.example.dat_lich_kham_fe.Firebase

data class ChatState(
    val isEnteringToken: Boolean = true,
    val remoteToken: String = "",
    val messageText: String = ""
)

