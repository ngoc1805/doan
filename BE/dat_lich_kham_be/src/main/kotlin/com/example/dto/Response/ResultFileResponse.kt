package com.example.dto.Response

import kotlinx.serialization.Serializable

@Serializable
data class ResultFileItem(
    val id : Int,
    val fileName: String,
    val filePath : String,
)

@Serializable
data class ListResultFileResponse(
    val resultfiles : List<ResultFileItem>
)
