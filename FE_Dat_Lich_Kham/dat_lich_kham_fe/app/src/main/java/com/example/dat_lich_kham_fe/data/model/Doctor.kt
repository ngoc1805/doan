package com.example.dat_lich_kham_fe.data.model

data class DoctorResponse(
    val id: Int,
    val name: String,
    val code: String,
    val examPrice: Int,
    val department: String
)
data class DoctorListResponse(
    val doctors: List<DoctorResponse>
)
