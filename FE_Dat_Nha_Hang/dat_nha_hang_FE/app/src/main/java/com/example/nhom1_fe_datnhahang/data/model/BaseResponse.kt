package com.example.nhom1_fe_datnhahang.data.model

import com.google.gson.JsonElement

data class BaseResponse(
    val success: Boolean,
    val message: String,
    val data: JsonElement? = null
)
