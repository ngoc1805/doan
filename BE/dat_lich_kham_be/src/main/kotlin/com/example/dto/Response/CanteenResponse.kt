package com.example.dto.Response

import kotlinx.serialization.Serializable

@Serializable
data class CanteenResponse(
    val userId: Int,
    val fmcToken: String
)
