package com.example.dat_lich_kham_fe.data.model

import kotlinx.serialization.Serializable

@Serializable
data class MealPaymentRequest(
    val userId: Int,
    val cycleId: Int,
    val amount: Int
)