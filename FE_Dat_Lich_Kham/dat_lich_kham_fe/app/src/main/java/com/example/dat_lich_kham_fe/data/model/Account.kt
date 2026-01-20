package com.example.dat_lich_kham_fe.data.model

data class UpdateFmcTokenRequest(
    val accountId: Int,
    val fcmToken: String
)

data class ChangePasswordRequest(
    val accountId: Int,
    val oldPassword: String?,
    val newPassword: String
)
