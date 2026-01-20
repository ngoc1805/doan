package com.example.dat_lich_kham_fe.data.model

import java.time.LocalDate

data class UserRequest(
    val accountId: Int,
    val fullName: String,
    val gender: String,
    val birthDate: String,
    val cccd: String,
    val hometown: String,
)

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

data class UpdateBalanceRequest(
    val userId: Int,
    val balance: Int
)

data class PinRequest (
    val userId: Int,
    val pinCode: String
)

data class CanteenResponse(
    val userId: Int,
    val fmcToken: String
)

