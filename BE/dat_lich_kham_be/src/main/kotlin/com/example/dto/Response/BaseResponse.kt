package com.example.dto.Response

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class BaseResponse(
    val success: Boolean,
    val message: String,
    val data: JsonElement? = null
)