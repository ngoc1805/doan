package com.example.dat_lich_kham_fe.data.model

data class SendOtpRequest(
    val phone: String
)

data class VerifyOtpForResetRequest(
    val phone: String,
    val otp: String
)

data class ResetPasswordRequest(
    val reset_token: String,
    val new_password: String
)

// ============ Response Models ============
data class SendOtpResponse(
    val message: String,
    val phone: String,
    val expires_in_minutes: Int
)

data class VerifyOtpForResetResponse(
    val message: String,
    val phone: String,
    val reset_token: String,
    val expires_in_minutes: Int
)

data class ResetPasswordResponse(
    val message: String,
    val phone: String
)

data class ResetPinRequest(
    val reset_token: String,
    val new_pin: String
)

data class ResetPinResponse(
    val message: String,
    val phone: String
)

data class ErrorResponse(
    val error: String
)
