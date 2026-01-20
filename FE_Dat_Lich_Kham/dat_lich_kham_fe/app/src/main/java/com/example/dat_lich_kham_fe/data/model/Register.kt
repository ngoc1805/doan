package com.example.dat_lich_kham_fe.data.model

data class RegisterRequest(
    val username: String,
    val password: String,
    val roleId: Int? = null // hoặc để mặc định là benhnhan
)
data class RegisterResponse(
    val username: String? = null,
    val message: String
)
