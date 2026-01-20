package com.example.dto.Response

import kotlinx.serialization.Serializable

@Serializable
data class ProfileResponse(
    val success: Boolean,
    val message: String,
    val data: ProfileData
)

@Serializable
data class ProfileData(
    val id: Int,
    val username: String,
    val role: String,
    val enabled: Int,
    val confirm: Int
)