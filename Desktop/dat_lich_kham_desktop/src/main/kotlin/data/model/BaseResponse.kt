package data.model

import kotlinx.serialization.json.JsonElement

data class BaseResponse(
    val success: Boolean,
    val message: String,
    val data: Any? = null
)