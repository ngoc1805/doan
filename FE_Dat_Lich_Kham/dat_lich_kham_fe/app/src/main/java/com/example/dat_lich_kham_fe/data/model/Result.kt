package com.example.dat_lich_kham_fe.data.model

data class ResultFileItem(
    val id : Int,
    val fileName: String,
    val filePath : String,
)

data class ListResultFileResponse(
    val resultfiles : List<ResultFileItem>
)

data class ResultItem(
    val appointmentId: Int,
    val fullName: String,
    val comment: String,
    val dietRecommendation: String?,
    val resultFiles : List<ResultFileItem>,
    val examDate: String
)

data class ListResultResponse(
    val results : List<ResultItem>
)

