package com.example.dto.Request

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val username: String,
    val password: String,
    val roleId: Int? = null // hoặc để mặc định là benhnhan
)
