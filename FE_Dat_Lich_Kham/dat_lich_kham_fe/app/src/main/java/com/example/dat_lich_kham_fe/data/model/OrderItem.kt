package com.example.dat_lich_kham_fe.data.model

data class OrderItemRequest (
    val orderId: Int,
    val menuId: Int,
    val quantity: Int
)
