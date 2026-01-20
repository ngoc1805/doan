package com.example.dto.Request

import kotlinx.serialization.Serializable

@Serializable
data class CreateServiceRoomRequest(
    val name: String,
    val code: String,
    val address: String,
    val examPrice: Int
)