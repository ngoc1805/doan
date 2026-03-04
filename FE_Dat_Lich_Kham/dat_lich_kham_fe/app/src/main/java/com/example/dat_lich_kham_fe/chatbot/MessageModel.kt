package com.example.dat_lich_kham_fe.chatbot


data class MessageModel(
    val id: Int,          // Thêm dòng này
    val message: String,
    val role: String,
)