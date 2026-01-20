package com.example.dat_lich_kham_fe.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Transaction(
    val id: Int,
    val userId: Int,
    val category: String,
    val transactionType: String,
    val amount: String,
    val isIncome: Boolean,
    val transactionTime: String
)