package com.example.dat_lich_kham_fe.data.model

import kotlinx.serialization.Serializable

data class PaymentRequest(
    val appointmentId: Int
)

data class PaymentUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val paymentDetails: PaymentDetails? = null
)

data class PaymentDetails(
    val appointmentId: Int,
    val userId: Int,
    val amountPaid: Double,
    val previousBalance: Double,
    val newBalance: Double,
    val transactionId: Int
)

data class DepositPaymentRequest(
    val userId: Int
)

data class DepositPaymentUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val depositDetails: DepositDetails? = null
)

data class DepositDetails(
    val userId: Int,
    val depositAmount: Double,
    val previousBalance: Double,
    val newBalance: Double,
    val transactionId: Int
)

data class RefundDepositRequest(
    val userId: Int
)

data class RefundDepositUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val refundDetails: RefundDetails? = null
)

data class RefundDetails(
    val userId: Int,
    val refundAmount: Double,
    val previousBalance: Double,
    val newBalance: Double,
    val transactionId: Int
)