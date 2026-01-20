package com.example.dat_lich_kham_fe.data.model

data class LoginRequest(
    val username: String,
    val password: String
)

//  Response cho đăng nhập thành công (200)
data class LoginResponse(
    val username: String? = null,
    val message: String
)

//  Response cho đăng nhập thất bại (401)
data class LoginErrorResponse(
    val message: String,
    val attemptsRemaining: Int? = null  // Số lần thử còn lại
)

//  Response cho tài khoản bị khóa (403)
data class LoginLockedResponse(
    val message: String,
    val remainingTime: Long,  // Thời gian còn lại (milliseconds)
    val isLocked: Boolean
)