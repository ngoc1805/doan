package com.example.dto.Request

import kotlinx.serialization.Serializable

@Serializable
data class UpdateBalanceRequest(
    val userId: Int,
    val balance: Int
)
