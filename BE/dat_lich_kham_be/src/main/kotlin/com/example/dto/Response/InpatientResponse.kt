package com.example.dto.Response

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class InpatientItem(
    val id: Int,
    val userId: Int,
    val fullname: String,
    val gender: String,
    val birthDate: LocalDate,
    val cccd: String,
    val hometown: String,
    val address: String?,
    val admissionDate: LocalDate?, // Ngày nhập viện
    val dischargeDate: LocalDate?, // Ngày xuất viện
    val status: String,
    val createAt: Instant
)

@Serializable
data class InpatientListResponse(
    val inpatients: List<InpatientItem>
)
