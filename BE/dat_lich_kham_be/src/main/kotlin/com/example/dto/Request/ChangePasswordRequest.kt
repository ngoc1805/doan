package com.example.dto.Request

import kotlinx.serialization.Serializable

@Serializable
data class ChangePasswordRequest(
    val accountId: Int,
    val oldPassword: String?,
    val newPassword: String
)