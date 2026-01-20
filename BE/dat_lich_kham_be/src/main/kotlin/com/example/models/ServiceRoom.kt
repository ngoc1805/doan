package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class ServiceRoom (
    val id: Int,
    val name: String,
    val code: String,
    val accountId: Int,
    val address: String,
    val examPrice: Int,
)