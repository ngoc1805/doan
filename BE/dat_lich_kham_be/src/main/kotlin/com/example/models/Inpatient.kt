package com.example.models

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Inpatient(
    val id: Int,
    val userId: Int,
    val address: String?,
    val status: String,
    val createdAt: Instant
)
