package com.example.dto.Response

import kotlinx.serialization.Serializable

@Serializable
data class RegisterResponse(
    val username: String?,
    val message: String
)
