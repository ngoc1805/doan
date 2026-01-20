package com.example.service

import kotlinx.coroutines.*
import java.time.LocalDateTime
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

data class ResetTokenData(
    val phone: String,
    val expiresAt: LocalDateTime
)

object ResetTokenService {
    private val tokenStorage = ConcurrentHashMap<String, ResetTokenData>()
    private const val TOKEN_EXPIRY_MINUTES = 10L

    init {
        // Tự động dọn dẹp token hết hạn mỗi 1 phút
        CoroutineScope(Dispatchers.Default).launch {
            while (isActive) {
                delay(60_000)
                cleanupExpiredTokens()
            }
        }
    }

    /**
     * Tạo token mới sau khi verify OTP thành công
     */
    fun generateToken(phone: String): String {
        val token = UUID.randomUUID().toString()
        val expiresAt = LocalDateTime.now().plusMinutes(TOKEN_EXPIRY_MINUTES)

        tokenStorage[token] = ResetTokenData(
            phone = phone,
            expiresAt = expiresAt
        )

        println("Tạo reset token cho $phone (hết hạn sau $TOKEN_EXPIRY_MINUTES phút)")
        return token
    }

    /**
     * Xác thực token và trả về phone number nếu hợp lệ
     */
    fun validateAndConsumeToken(token: String): String? {
        val tokenData = tokenStorage[token]

        if (tokenData == null) {
            println("Token không tồn tại: $token")
            return null
        }

        if (LocalDateTime.now().isAfter(tokenData.expiresAt)) {
            tokenStorage.remove(token)
            println("Token đã hết hạn: $token")
            return null
        }

        // Token hợp lệ → Xóa token (chỉ dùng 1 lần)
        tokenStorage.remove(token)
        println("Token hợp lệ cho ${tokenData.phone}")
        return tokenData.phone
    }

    /**
     * Dọn dẹp token hết hạn
     */
    private fun cleanupExpiredTokens() {
        val now = LocalDateTime.now()
        val expiredTokens = tokenStorage.filter { (_, data) ->
            now.isAfter(data.expiresAt)
        }.keys

        expiredTokens.forEach { tokenStorage.remove(it) }

        if (expiredTokens.isNotEmpty()) {
            println("Đã xóa ${expiredTokens.size} reset token hết hạn")
        }
    }
}