package com.example.dto.Request

import kotlinx.serialization.Serializable

@Serializable
data class UpdateDisplayRequest(
    val id: Int,
    val isDisplay: Boolean
)
