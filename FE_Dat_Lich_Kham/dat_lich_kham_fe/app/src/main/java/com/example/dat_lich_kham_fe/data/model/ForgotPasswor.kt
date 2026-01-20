package com.example.dat_lich_kham_fe.data.model

data class ForgotPasswordUiState(
    val isLoading: Boolean = false,
    val phone: String = "",
    val otp: String = "",
    val resetToken: String = "",
    val errorMessage: String? = null,
    val successMessage: String? = null,

    // OTP specific
    val otpSent: Boolean = false,
    val otpExpiresInMinutes: Int = 5,
    val remainingSeconds: Int = 300, // 5 phút = 300 giây

    // Verification
    val isVerified: Boolean = false
)

data class ResetPasswordUiState(
    val isLoading: Boolean = false,
    val newPassword: String = "",
    val confirmPassword: String = "",
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val resetSuccess: Boolean = false
)

data class ForgotPinUiState(
    val isLoading: Boolean = false,
    val phone: String = "",
    val otp: String = "",
    val resetToken: String = "",
    val errorMessage: String? = null,
    val successMessage: String? = null,

    // OTP specific
    val otpSent: Boolean = false,
    val otpExpiresInMinutes: Int = 5,
    val remainingSeconds: Int = 300,

    // Verification
    val isVerified: Boolean = false
)

data class ResetPinUiState(
    val isLoading: Boolean = false,
    val newPin: String = "",
    val confirmPin: String = "",
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val resetSuccess: Boolean = false
)