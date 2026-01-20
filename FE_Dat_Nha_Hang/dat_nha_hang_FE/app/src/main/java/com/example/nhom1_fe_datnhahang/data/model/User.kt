package com.example.nhom1_fe_datnhahang.data.model

data class UserResponse(
    val Id: Int,
    val fullName: String,
    val gender: String,
    val birthDate: String,
    val cccd: String,
    val hometown: String,
    val balance: Int,
    val imageUrl: String?,
)
