package com.example.dto.Request

import kotlinx.serialization.Serializable

@Serializable
data class UpdateFmcTokenRequest(
    val accountId: Int,
    val fcmToken: String
)
