package com.example.dat_lich_kham_fe.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.dat_lich_kham_fe.data.api.RetrofitInstance
import com.example.dat_lich_kham_fe.data.model.ErrorResponse
import com.example.dat_lich_kham_fe.data.model.ForgotPinUiState
import com.example.dat_lich_kham_fe.data.model.VerifyOtpForResetRequest
import com.example.dat_lich_kham_fe.data.model.ResetPinRequest
import com.example.dat_lich_kham_fe.data.model.ResetPinUiState
import com.example.dat_lich_kham_fe.data.model.SendOtpRequest
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ForgotPinViewModel(private val context: Context) : ViewModel() {
    private val otpApi by lazy { RetrofitInstance.otpApi(context) }

    private val _uiState = MutableStateFlow(ForgotPinUiState())
    val uiState: StateFlow<ForgotPinUiState> = _uiState.asStateFlow()

    private val _resetPinState = MutableStateFlow(ResetPinUiState())
    val resetPinState: StateFlow<ResetPinUiState> = _resetPinState.asStateFlow()

    // ============ Màn 1: Verify OTP (Giống ForgotPassword) ============

    fun updatePhone(phone: String) {
        _uiState.value = _uiState.value.copy(phone = phone)
    }

    fun updateOtp(otp: String) {
        _uiState.value = _uiState.value.copy(otp = otp)
    }

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

    // ============ Màn 2: Reset PIN (Dùng NumberPad) ============

    fun updateNewPin(pin: String) {
        _resetPinState.value = _resetPinState.value.copy(newPin = pin)
    }

    fun updateConfirmPin(pin: String) {
        _resetPinState.value = _resetPinState.value.copy(confirmPin = pin)
    }

    fun resetPin(resetToken: String, onSuccess: () -> Unit) {
        val newPin = _resetPinState.value.newPin
        val confirmPin = _resetPinState.value.confirmPin

        if (newPin.isBlank()) {
            _resetPinState.value = _resetPinState.value.copy(
                errorMessage = "Vui lòng nhập mã PIN mới"
            )
            return
        }

        if (!newPin.matches(Regex("^\\d{6}$"))) {
            _resetPinState.value = _resetPinState.value.copy(
                errorMessage = "Mã PIN phải là 6 chữ số"
            )
            return
        }

        if (newPin != confirmPin) {
            _resetPinState.value = _resetPinState.value.copy(
                errorMessage = "Mã PIN xác nhận không khớp"
            )
            return
        }

        viewModelScope.launch {
            _resetPinState.value = _resetPinState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            try {
                val response = otpApi.resetPin(ResetPinRequest(resetToken, newPin))

                if (response.isSuccessful) {
                    val body = response.body()
                    _resetPinState.value = _resetPinState.value.copy(
                        isLoading = false,
                        resetSuccess = true,
                        successMessage = body?.message ?: "Đổi mã PIN thành công"
                    )
                    onSuccess()
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        Gson().fromJson(errorBody, ErrorResponse::class.java).error
                    } catch (e: Exception) {
                        "Không thể đổi mã PIN. Vui lòng thử lại"
                    }

                    _resetPinState.value = _resetPinState.value.copy(
                        isLoading = false,
                        errorMessage = errorMessage
                    )
                }
            } catch (e: Exception) {
                _resetPinState.value = _resetPinState.value.copy(
                    isLoading = false,
                    errorMessage = "Lỗi kết nối: ${e.message}"
                )
            }
        }
    }

    fun clearNewPin() {
        _resetPinState.value = _resetPinState.value.copy(newPin = "")
    }

    fun clearConfirmPin() {
        _resetPinState.value = _resetPinState.value.copy(confirmPin = "")
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
        _resetPinState.value = _resetPinState.value.copy(errorMessage = null)
    }
}

// ============ Factory ============
class ForgotPinViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ForgotPinViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ForgotPinViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}