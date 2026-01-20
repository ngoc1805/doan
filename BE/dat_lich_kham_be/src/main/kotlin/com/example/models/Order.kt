package com.example.models

import kotlinx.serialization.Serializable
import kotlinx.datetime.Instant

@Serializable
data class Order(
    val id: Int,
    val userId: Int,
    val phone: String,
    val address: String,
    val note: String,
    val status: String,
    val imageUrl: String,
    val createdAt: Instant
)