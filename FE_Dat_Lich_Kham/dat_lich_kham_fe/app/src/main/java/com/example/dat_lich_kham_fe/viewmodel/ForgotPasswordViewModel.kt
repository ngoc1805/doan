package com.example.dat_lich_kham_fe.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.dat_lich_kham_fe.data.api.RetrofitInstance
import com.example.dat_lich_kham_fe.data.model.ErrorResponse
import com.example.dat_lich_kham_fe.data.model.ForgotPasswordUiState
import com.example.dat_lich_kham_fe.data.model.ResetPasswordRequest
import com.example.dat_lich_kham_fe.data.model.ResetPasswordUiState
import com.example.dat_lich_kham_fe.data.model.SendOtpRequest
import com.example.dat_lich_kham_fe.data.model.VerifyOtpForResetRequest
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(private val context: Context) : ViewModel() {
    private val otpApi by lazy { RetrofitInstance.otpApi(context) }

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    private val _resetPasswordState = MutableStateFlow(ResetPasswordUiState())
    val resetPasswordState: StateFlow<ResetPasswordUiState> = _resetPasswordState.asStateFlow()

    // ============ Màn 1: Verify OTP ============

    fun updatePhone(phone: String) {
        _uiState.value = _uiState.value.copy(phone = phone)
    }

    fun updateOtp(otp: String) {
        _uiState.value = _uiState.value.copy(otp = otp)
    }

    /**
     * Gửi OTP
     */
    fun sendOtp() {
        val phone = _uiState.value.phone.trim()

        if (phone.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Vui lòng nhập số điện thoại")
            return
        }

        if (!phone.matches(Regex("^0\\d{9}$"))) {
            _uiState.value = _uiState.value.copy(errorMessage = "Số điện thoại không hợp lệ (10 số, bắt đầu bằng 0)")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                val response = otpApi.sendOtp(SendOtpRequest(phone))

                if (response.isSuccessful) {
                    val body = response.body()
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        otpSent = true,
                        successMessage = body?.message ?: "Đã gửi OTP thành công",
                        otpExpiresInMinutes = body?.expires_in_minutes ?: 5,
                        remainingSeconds = (body?.expires_in_minutes ?: 5) * 60
                    )
                    startCountdown()
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        Gson().fromJson(errorBody, ErrorResponse::class.java).error
                    } catch (e: Exception) {
                        "Không thể gửi OTP. Vui lòng thử lại"
                    }

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = errorMessage
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Lỗi kết nối: ${e.message}"
                )
            }
        }
    }

    /**
     * Xác thực OTP và nhận reset token
     */
    fun verifyOtp(onSuccess: (String) -> Unit) {
        val phone = _uiState.value.phone.trim()
        val otp = _uiState.value.otp.trim()

        if (otp.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Vui lòng nhập mã OTP")
            return
        }

        if (!otp.matches(Regex("^\\d{6}$"))) {
            _uiState.value = _uiState.value.copy(errorMessage = "OTP phải là 6 chữ số")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                val response = otpApi.verifyOtpForReset(VerifyOtpForResetRequest(phone, otp))

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isVerified = true,
                            resetToken = body.reset_token,
                            successMessage = body.message
                        )
                        onSuccess(body.reset_token)
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        Gson().fromJson(errorBody, ErrorResponse::class.java).error
                    } catch (e: Exception) {
                        "OTP không đúng hoặc đã hết hạn"
                    }

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = errorMessage
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Lỗi kết nối: ${e.message}"
                )
            }
        }
    }

    /**
     * Đếm ngược thời gian OTP
     */
    private fun startCountdown() {
        viewModelScope.launch {
            while (_uiState.value.remainingSeconds > 0) {
                kotlinx.coroutines.delay(1000)
                _uiState.value = _uiState.value.copy(
                    remainingSeconds = _uiState.value.remainingSeconds - 1
                )
            }
        }
    }

    // ============ Màn 2: Reset Password ============

    fun updateNewPassword(password: String) {
        _resetPasswordState.value = _resetPasswordState.value.copy(newPassword = password)
    }

    fun updateConfirmPassword(password: String) {
        _resetPasswordState.value = _resetPasswordState.value.copy(confirmPassword = password)
    }

    /**
     * Reset mật khẩu với token
     */
    fun resetPassword(resetToken: String, onSuccess: () -> Unit) {
        val newPassword = _resetPasswordState.value.newPassword
        val confirmPassword = _resetPasswordState.value.confirmPassword

        if (newPassword.isBlank()) {
            _resetPasswordState.value = _resetPasswordState.value.copy(
                errorMessage = "Vui lòng nhập mật khẩu mới"
            )
            return
        }

        if (newPassword.length < 6) {
            _resetPasswordState.value = _resetPasswordState.value.copy(
                errorMessage = "Mật khẩu phải có ít nhất 6 ký tự"
            )
            return
        }

        if (newPassword != confirmPassword) {
            _resetPasswordState.value = _resetPasswordState.value.copy(
                errorMessage = "Mật khẩu xác nhận không khớp"
            )
            return
        }

        viewModelScope.launch {
            _resetPasswordState.value = _resetPasswordState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            try {
                val response = otpApi.resetPassword(ResetPasswordRequest(resetToken, newPassword))

                if (response.isSuccessful) {
                    val body = response.body()
                    _resetPasswordState.value = _resetPasswordState.value.copy(
                        isLoading = false,
                        resetSuccess = true,
                        successMessage = body?.message ?: "Đổi mật khẩu thành công"
                    )
                    onSuccess()
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        Gson().fromJson(errorBody, ErrorResponse::class.java).error
                    } catch (e: Exception) {
                        "Không thể đổi mật khẩu. Vui lòng thử lại"
                    }

                    _resetPasswordState.value = _resetPasswordState.value.copy(
                        isLoading = false,
                        errorMessage = errorMessage
                    )
                }
            } catch (e: Exception) {
                _resetPasswordState.value = _resetPasswordState.value.copy(
                    isLoading = false,
                    errorMessage = "Lỗi kết nối: ${e.message}"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
        _resetPasswordState.value = _resetPasswordState.value.copy(errorMessage = null)
    }
}

// ============ Factory ============
class ForgotPasswordViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ForgotPasswordViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ForgotPasswordViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}