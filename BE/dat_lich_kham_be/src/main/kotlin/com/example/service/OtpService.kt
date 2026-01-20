package com.example.service

import kotlinx.coroutines.*
import org.mindrot.jbcrypt.BCrypt
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap
import kotlin.random.Random

data class HashedOtpData(
    val hashedOtp: String,
    val expiresAt: LocalDateTime,
    var attempts: Int = 0
)

object OtpService {
    private val otpStorage = ConcurrentHashMap<String, HashedOtpData>()
    private val OTP_EXPIRY_MINUTES = 5L
    private const val MAX_ATTEMPTS = 5

    // PEPPER - NÊN LƯU TRONG BIẾN MÔI TRƯỜNG
    private val PEPPER = System.getenv("OTP_PEPPER")
        ?: "MyServerSecretPepper_ChangeThis_XYZ123"

    init {
        CoroutineScope(Dispatchers.Default).launch {
            while (isActive) {
                delay(60_000)
                cleanupExpiredOtps()
            }
        }
    }

    fun generateOtp(phoneNumber: String): String {
        val otp = String.format("%06d", Random.nextInt(0, 999999))
        val expiresAt = LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES)

        val otpWithPepper = "$otp:$PEPPER"
        val hashedOtp = BCrypt.hashpw(otpWithPepper, BCrypt.gensalt())

        otpStorage[phoneNumber] = HashedOtpData(
            hashedOtp = hashedOtp,
            expiresAt = expiresAt,
            attempts = 0
        )

        println("OTP tạo cho $phoneNumber (đã hash với pepper)")
        return otp
    }

    fun verifyAndRemoveOtp(phoneNumber: String, inputOtp: String): VerifyResult {
        val otpData = otpStorage[phoneNumber]

        if (otpData == null) {
            println("Không tìm thấy OTP cho $phoneNumber")
            return VerifyResult.NOT_FOUND
        }

        if (LocalDateTime.now().isAfter(otpData.expiresAt)) {
            otpStorage.remove(phoneNumber)
            println("OTP của $phoneNumber đã hết hạn")
            return VerifyResult.EXPIRED
        }

        if (otpData.attempts >= MAX_ATTEMPTS) {
            otpStorage.remove(phoneNumber)
            println("$phoneNumber đã vượt quá $MAX_ATTEMPTS lần thử")
            return VerifyResult.TOO_MANY_ATTEMPTS
        }

        val inputWithPepper = "$inputOtp:$PEPPER"
        val isValid = BCrypt.checkpw(inputWithPepper, otpData.hashedOtp)

        if (isValid) {
            otpStorage.remove(phoneNumber)
            println("OTP xác thực thành công cho $phoneNumber")
            return VerifyResult.SUCCESS
        } else {
            otpData.attempts++
            println("OTP sai cho $phoneNumber (lần thử ${otpData.attempts}/$MAX_ATTEMPTS)")
            return VerifyResult.INVALID
        }
    }

    private fun cleanupExpiredOtps() {
        val now = LocalDateTime.now()
        val expiredKeys = otpStorage.filter { (_, data) ->
            now.isAfter(data.expiresAt)
        }.keys

        expiredKeys.forEach { otpStorage.remove(it) }

        if (expiredKeys.isNotEmpty()) {
            println("🗑️ Đã xóa ${expiredKeys.size} OTP hết hạn")
        }
    }
}

enum class VerifyResult {
    SUCCESS,
    INVALID,
    EXPIRED,
    NOT_FOUND,
    TOO_MANY_ATTEMPTS,
    ERROR
}