package com.example.dto.Response

import kotlinx.serialization.Serializable

@Serializable
data class DoctorListItem(
    val id: Int,
    val name: String,
    val code: String,
    val examPrice: Int,
    val department: String
)

@Serializable
data class DoctorListResponse(
    val doctors: List<DoctorListItem>
)