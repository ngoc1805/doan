package com.example.dto.Request

import kotlinx.serialization.Serializable

@Serializable
data class PinRequest (
    val userId: Int,
    val pinCode: String
)