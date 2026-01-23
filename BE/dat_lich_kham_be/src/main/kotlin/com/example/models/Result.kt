package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class Result(
    val id: Int,
    val appointmentId: Int,
    val comment: String,
    val dietRecommendation: String?
)