package com.example.dto.Request

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class MealSubscriptionRequest(
    val inpatientId: Int
)

@Serializable
data class MealRenewalRequest(
    val inpatientId: Int
)

@Serializable
data class MealSkipRequest(
    val inpatientId: Int,
    val skipDate: LocalDate
)

@Serializable
data class MealPaymentRequest(
    val cycleId: Int,
    val paymentAmount: Int,
    val paymentMethod: String
)