package com.example.dto.Response

import com.example.models.Account
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val username: String? = null,
    val message: String
)