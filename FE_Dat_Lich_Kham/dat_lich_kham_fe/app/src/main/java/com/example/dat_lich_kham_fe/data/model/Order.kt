package com.example.dat_lich_kham_fe.data.model

data class OrderRequest(
    val userId: Int,
    val phone: String,
    val address: String,
    val note: String,
    val status: String,
)

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

data class OrderItemDetail(
    val menuName: String,
    val quantity: Int
)

