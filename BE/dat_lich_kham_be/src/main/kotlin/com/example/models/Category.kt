package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val id: Int,
    val name: String
)