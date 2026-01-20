package com.example.nhom1_fe_datnhahang.data.model

data class UpdateFmcTokenRequest(
    val accountId: Int,
    val fcmToken: String
)

data class ChangePasswordRequest(
    val accountId: Int,
    val oldPassword: String?,
    val newPassword: String
)
