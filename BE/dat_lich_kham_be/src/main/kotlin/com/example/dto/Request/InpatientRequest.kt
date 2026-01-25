package com.example.dto.Request

import kotlinx.serialization.Serializable

@Serializable
data class InpatientRequest(
    val userId: Int,
    val appointmentId: Int? = null // Thêm appointmentId
)

@Serializable
data class UpdateStatusInpatient(
    val id: Int
)

@Serializable
data class UpdateAddressRequest(
    val id: Int,
    val address: String
)