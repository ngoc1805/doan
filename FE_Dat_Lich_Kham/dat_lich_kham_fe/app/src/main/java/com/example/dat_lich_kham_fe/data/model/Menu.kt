package com.example.dat_lich_kham_fe.data.model

data class Menu(
    val id: Int,
    val name: String,
    val examPrice: Int,
    val description: String,
    val category: String,
    var isDisplay: Boolean,
    val imageUrl: String
)

data class MenuListResponse(
    val menus: List<Menu>
)
