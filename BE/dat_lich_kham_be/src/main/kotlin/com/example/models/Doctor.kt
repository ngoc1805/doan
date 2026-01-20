package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class Doctor(
    val id: Int,
    val name: String,
    val code: String,
    val accountId: Int,
    val departmentId: Int,
    val examPrice: Int,
    val balance: Int
)