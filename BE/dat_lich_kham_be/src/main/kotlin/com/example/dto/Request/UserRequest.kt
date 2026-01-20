package com.example.dto.Request

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class UserRequest(
    val accountId: Int,
    val fullName: String,
    val gender: String,
    val birthDate: LocalDate,
    val cccd: String,
    val hometown: String,
)
