package com.example.dto.Request

import kotlinx.serialization.Serializable

@Serializable
data class OrderRequest(
    val userId: Int,
    val phone: String,
    val address: String,
    val note: String ?= null,
    val status: String,
)
