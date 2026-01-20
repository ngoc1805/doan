package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class ResultFile(
    val id: Int,
    val appointmentId: Int,
    val fileName: String,
    val filePath: String,
    val fileType: String,

    // Thêm các field cho chữ ký số
    val isSigned: Boolean = false,
    val signedFilePath: String? = null,
    val signatureHash: String? = null,
    val signedByDoctorId: Int? = null,
    val signedByDoctorName: String? = null,
    val signedAt: String? = null
)