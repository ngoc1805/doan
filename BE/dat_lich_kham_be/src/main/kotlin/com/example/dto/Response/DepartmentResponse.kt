package com.example.dto.Response

import kotlinx.serialization.Serializable

@Serializable
data class DepartmentResponse (
    val id : Int,
    val name : String,
    val description : String
)