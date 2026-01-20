package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class JWTPayload(
    val id: Int,
    val username: String,
    val role: String,
    val enabled: Byte,
    val confirm: Byte
)
