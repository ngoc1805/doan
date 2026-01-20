package com.example.models

import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class Department(
    val id: Int,
    val name: String,
    val description: String,
)