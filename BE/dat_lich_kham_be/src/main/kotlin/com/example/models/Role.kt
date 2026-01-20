package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class Role(
    val roleId: Int,
    val roleName: String,
    val description: String? = null
)