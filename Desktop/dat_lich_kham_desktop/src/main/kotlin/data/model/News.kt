package data.model

data class News(
    val title: String,
    val description: String,
    val imageUrl: String = "",
    val link: String,
)