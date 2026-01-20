package data.model

data class NotificationRequest(
    val userId: Int,
    val content: String,
    val path: String
)