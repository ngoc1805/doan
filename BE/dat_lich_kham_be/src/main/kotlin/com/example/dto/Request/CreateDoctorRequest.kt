package com.example.dto.Request

import kotlinx.serialization.Serializable

@Serializable
data class CreateDoctorRequest(
    val name: String,
    val code: String,
    val departmentId: Int,
    val examPrice: Int
)