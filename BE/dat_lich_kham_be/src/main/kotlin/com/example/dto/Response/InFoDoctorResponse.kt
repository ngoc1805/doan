package com.example.dto.Response

import kotlinx.serialization.Serializable

@Serializable
data class InFoDoctorResponse(
    val id: Int,
    val name: String,
    val code: String,
    val examPrice: Int,
    val department: String,
    val balance : Int
)