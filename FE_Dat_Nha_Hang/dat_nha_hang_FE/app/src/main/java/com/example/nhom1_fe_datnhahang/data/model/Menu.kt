package com.example.nhom1_fe_datnhahang.data.model

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

data class UpdateDisplayRequest(
    val id: Int,
    val isDisplay: Boolean
)

