package data.model

data class AccountInfo(
    val accountId: Int,
    val username: String,
    val role: String
)
data class ChangePasswordRequest(
    val accountId: Int,
    val oldPassword: String?,
    val newPassword: String
)
