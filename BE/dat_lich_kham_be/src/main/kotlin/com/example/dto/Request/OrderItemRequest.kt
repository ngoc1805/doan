package com.example.dto.Request

import kotlinx.serialization.Serializable

@Serializable
data class OrderItemRequest (
    val orderId: Int,
    val menuId: Int,
    val quantity: Int
)