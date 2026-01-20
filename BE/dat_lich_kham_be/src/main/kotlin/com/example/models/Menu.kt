package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class Menu(
    val id: Int,
    val name: String,
    val examPrice: Int,
    val description: String,
    val category: String,
    val isDisplay: Boolean,
    val imageUrl: String
)