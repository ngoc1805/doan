package data.model

data class LoginRequest(
    val username: String,
    val password: String
)
data class LoginResponse(
    val username: String? = null,
    val message: String
)