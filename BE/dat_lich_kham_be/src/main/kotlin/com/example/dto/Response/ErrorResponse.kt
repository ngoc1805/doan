package com.example.dto.Response

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val success: Boolean,
    val message: String
)