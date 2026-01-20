package com.example.dto.Request

import kotlinx.serialization.Serializable

@Serializable
data class UploadFileRequest(
    val appointmentId: Int
)