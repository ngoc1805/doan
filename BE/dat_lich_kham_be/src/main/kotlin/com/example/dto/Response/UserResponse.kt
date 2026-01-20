package com.example.dto.Response

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val Id: Int,
    val fullName: String,
    val gender: String,
    val birthDate: LocalDate,
    val cccd: String,
    val hometown: String,
    val balance: Int,
    val imageurl: String?
)
