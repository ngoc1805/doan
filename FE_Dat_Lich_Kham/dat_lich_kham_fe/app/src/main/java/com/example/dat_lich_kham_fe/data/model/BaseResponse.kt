package com.example.dat_lich_kham_fe.data.model

import com.google.gson.JsonElement

data class BaseResponse(
    val success: Boolean,
    val message: String,
    val data: JsonElement? = null
)
