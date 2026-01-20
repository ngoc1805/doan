package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class OrderItem(
    val id: Int,
    val orderId: Int,
    val menuId: Int,
    val quantity: Int
)