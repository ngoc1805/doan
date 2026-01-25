package data.model

data class ResultRequest(
    val appointmentId: Int,
    val comment: String,
    val dietRecommendation: String? = null // Chỉ định ăn uống
)