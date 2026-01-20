package com.example.dat_lich_kham_fe.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dat_lich_kham_fe.data.model.DepositDetails
import com.example.dat_lich_kham_fe.data.model.DepositPaymentUiState
import com.example.dat_lich_kham_fe.data.model.PaymentDetails
import com.example.dat_lich_kham_fe.data.model.PaymentUiState
import com.example.dat_lich_kham_fe.data.model.RefundDepositUiState
import com.example.dat_lich_kham_fe.data.model.RefundDetails
import com.example.dat_lich_kham_fe.data.repository.PaymentRepository
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class PaymentViewModel(
    private val context: Context
) : ViewModel() {
    private val paymentRepository =  PaymentRepository(context)

    private val _uiState = MutableStateFlow(PaymentUiState())
    val uiState: StateFlow<PaymentUiState> = _uiState.asStateFlow()

    private val _depositUiState = MutableStateFlow(DepositPaymentUiState())
    val depositUiState: StateFlow<DepositPaymentUiState> = _depositUiState.asStateFlow()

    private val _refundUiState = MutableStateFlow(RefundDepositUiState())
    val refundUiState: StateFlow<RefundDepositUiState> = _refundUiState.asStateFlow()

    fun payAppointment(appointmentId: Int) {
        viewModelScope.launch {
            try {
                _uiState.value = PaymentUiState(isLoading = true)

                val response = paymentRepository.paymentAppointment(appointmentId)

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.success == true) {
                        // Parse payment details từ data
                        val paymentDetails = body.data?.asJsonObject?.let { data ->
                            PaymentDetails(
                                appointmentId = data.get("appointmentId")?.asInt ?: appointmentId,
                                userId = data.get("userId")?.asInt ?: 0,
                                amountPaid = data.get("amountPaid")?.asDouble ?: 0.0,
                                previousBalance = data.get("previousBalance")?.asDouble ?: 0.0,
                                newBalance = data.get("newBalance")?.asDouble ?: 0.0,
                                transactionId = data.get("transactionId")?.asInt ?: -1
                            )
                        }

                        _uiState.value = PaymentUiState(
                            isLoading = false,
                            isSuccess = true,
                            paymentDetails = paymentDetails
                        )
                    } else {
                        // Server trả về success = false
                        val message = body?.message ?: "Thanh toán thất bại"
                        _uiState.value = PaymentUiState(
                            isLoading = false,
                            isSuccess = false,
                            errorMessage = message
                        )
                    }
                } else {
                    // HTTP error
                    val errorMsg = when (response.code()) {
                        400 -> "Số dư không đủ hoặc dữ liệu không hợp lệ"
                        404 -> "Không tìm thấy lịch hẹn"
                        500 -> "Lỗi server, vui lòng thử lại"
                        else -> "Lỗi kết nối: ${response.code()}"
                    }
                    _uiState.value = PaymentUiState(
                        isLoading = false,
                        isSuccess = false,
                        errorMessage = errorMsg
                    )
                }
            } catch (e: Exception) {
                _uiState.value = PaymentUiState(
                    isLoading = false,
                    isSuccess = false,
                    errorMessage = "Lỗi: ${e.message ?: "Không thể kết nối đến server"}"
                )
            }
        }
    }

    fun payDeposit(userId: Int) {
        viewModelScope.launch {
            try {
                _depositUiState.value = DepositPaymentUiState(isLoading = true)

                val response = paymentRepository.paymentDeposit(userId)

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.success == true) {
                        val depositDetails = body.data?.asJsonObject?.let { data ->
                            DepositDetails(
                                userId = data.get("userId")?.asInt ?: userId,
                                depositAmount = data.get("depositAmount")?.asDouble ?: 0.0,
                                previousBalance = data.get("previousBalance")?.asDouble ?: 0.0,
                                newBalance = data.get("newBalance")?.asDouble ?: 0.0,
                                transactionId = data.get("transactionId")?.asInt ?: -1
                            )
                        }

                        _depositUiState.value = DepositPaymentUiState(
                            isLoading = false,
                            isSuccess = true,
                            depositDetails = depositDetails
                        )
                    } else {
                        val message = body?.message ?: "Thanh toán tiền cọc thất bại"
                        _depositUiState.value = DepositPaymentUiState(
                            isLoading = false,
                            isSuccess = false,
                            errorMessage = message
                        )
                    }
                } else {
                    val errorMsg = when (response.code()) {
                        400 -> "Số dư không đủ để thanh toán tiền cọc 100,000đ"
                        404 -> "Không tìm thấy người dùng"
                        500 -> "Lỗi server, vui lòng thử lại"
                        else -> "Lỗi kết nối: ${response.code()}"
                    }
                    _depositUiState.value = DepositPaymentUiState(
                        isLoading = false,
                        isSuccess = false,
                        errorMessage = errorMsg
                    )
                }
            } catch (e: Exception) {
                _depositUiState.value = DepositPaymentUiState(
                    isLoading = false,
                    isSuccess = false,
                    errorMessage = "Lỗi: ${e.message ?: "Không thể kết nối đến server"}"
                )
            }
        }
    }
    fun refundDeposit(userId: Int) {
        viewModelScope.launch {
            try {
                _refundUiState.value = RefundDepositUiState(isLoading = true)

                val response = paymentRepository.refundDeposit(userId)

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.success == true) {
                        val refundDetails = body.data?.asJsonObject?.let { data ->
                            RefundDetails(
                                userId = data.get("userId")?.asInt ?: userId,
                                refundAmount = data.get("refundAmount")?.asDouble ?: 0.0,
                                previousBalance = data.get("previousBalance")?.asDouble ?: 0.0,
                                newBalance = data.get("newBalance")?.asDouble ?: 0.0,
                                transactionId = data.get("transactionId")?.asInt ?: -1
                            )
                        }

                        _refundUiState.value = RefundDepositUiState(
                            isLoading = false,
                            isSuccess = true,
                            refundDetails = refundDetails
                        )
                    } else {
                        val message = body?.message ?: "Hoàn tiền cọc thất bại"
                        _refundUiState.value = RefundDepositUiState(
                            isLoading = false,
                            isSuccess = false,
                            errorMessage = message
                        )
                    }
                } else {
                    val errorMsg = when (response.code()) {
                        404 -> "Không tìm thấy người dùng"
                        500 -> "Lỗi server, vui lòng thử lại"
                        else -> "Lỗi kết nối: ${response.code()}"
                    }
                    _refundUiState.value = RefundDepositUiState(
                        isLoading = false,
                        isSuccess = false,
                        errorMessage = errorMsg
                    )
                }
            } catch (e: Exception) {
                _refundUiState.value = RefundDepositUiState(
                    isLoading = false,
                    isSuccess = false,
                    errorMessage = "Lỗi: ${e.message ?: "Không thể kết nối đến server"}"
                )
            }
        }
    }

    fun resetDepositState() {
        _depositUiState.value = DepositPaymentUiState()
    }

    fun resetRefundState() {
        _refundUiState.value = RefundDepositUiState()
    }

    // Reset state
    fun resetState() {
        _uiState.value = PaymentUiState()
    }
}