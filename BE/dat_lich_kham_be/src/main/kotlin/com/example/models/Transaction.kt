package com.example.models

import kotlinx.serialization.Serializable
import kotlinx.datetime.Instant

@Serializable
data class Transaction(
    val id: Int,
    val userId: Int,
    val category: String,
    val transactionType: String,
    val amount: String,
    val isIncome: Boolean,
    val transactionTime: Instant
)