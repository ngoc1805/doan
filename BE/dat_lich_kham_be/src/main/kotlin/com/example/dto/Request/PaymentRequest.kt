package com.example.dto.Request

import kotlinx.serialization.Serializable

@Serializable
data class PaymentRequest(
    val appointmentId: Int
)

@Serializable
data class DepositPaymentRequest(
    val userId: Int
)