package com.example.dto.Request

import kotlinx.serialization.Serializable

@Serializable
data class CreateDepartmentRequest(
    val name: String,
    val description: String,
)
