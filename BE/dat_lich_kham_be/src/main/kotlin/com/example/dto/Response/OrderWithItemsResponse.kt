package com.example.dto.Response

import kotlinx.serialization.Serializable

@Serializable
data class OrderWithItemsResponse(
    val id: Int,
    val userId: Int,
    val userName: String,
    val fmctoken: String,
    val phone: String,
    val address: String,
    val note: String,
    val status: String,
    val items: List<OrderItemDetail>,
    val totalPrice: Int,
    val imageUrl: String
)

@Serializable
data class OrderItemDetail(
    val menuName: String,
    val quantity: Int
)