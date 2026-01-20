package com.example.dto.Response

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class ResultItem(
    val appointmentId: Int,
    val fullName: String,
    val comment: String,
    val resultFiles : List<ResultFileItem>,
    val examDate: LocalDate
)

@Serializable
data class ListResultResponse(
    val results : List<ResultItem>
)
