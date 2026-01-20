package com.example.models

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class Users(
    val id: Int,
    val accountId: Int,
    val fullName: String,
    val gender: String,
    val birthDate: LocalDate,
    val cccd: String,
    val hometown: String,
    val balance: Int,
    val pincode: String?,
    val imageurl: String?,
    val phone: String?
)
