package com.example.routes.route

import com.example.repository.AccountRepository
import com.example.service.OtpGatewayService
import com.example.service.OtpService
import com.example.service.ResetTokenService
import com.example.service.VerifyResult
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*

fun Route.otpRoutes(accountRepository: AccountRepository) {

    // Route gửi OTP (GIỮ NGUYÊN)
    post("/send_otp") {
        try {
            val data = call.receive<SendOtpRequest>()

            if (data.phone.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, mapOf(
                    "error" to "Số điện thoại không được để trống"
                ))
                return@post
            }

            val account = accountRepository.findByPhone(data.phone)
            if (account == null) {
                call.respond(HttpStatusCode.NotFound, mapOf(
                    "error" to "Số điện thoại chưa được đăng ký"
                ))
                return@post
            }

            val otp = OtpService.generateOtp(data.phone)
            println("📱 Tạo OTP cho ${data.phone} (đã hash với pepper)")

            val ok = OtpGatewayService.sendOtpViaGateway(data.phone, otp)

            if (ok) {
                call.respond(HttpStatusCode.OK, SendOtpResponse(
                    message = "Đã gửi OTP thành công!",
                    phone = data.phone,
                    expires_in_minutes = 5
                ))
            } else {
                call.respond(HttpStatusCode.InternalServerError, mapOf(
                    "error" to "Không gửi được OTP!"
                ))
            }
        } catch (e: Exception) {
            println("Lỗi gửi OTP: ${e.message}")
            e.printStackTrace()
            call.respond(HttpStatusCode.InternalServerError, mapOf(
                "error" to "Lỗi server: ${e.message}"
            ))
        }
    }

    // Route xác thực OTP (GIỮ NGUYÊN - dùng cho login 2FA)
    post("/verify_otp") {
        try {
            val data = call.receive<VerifyOtpRequest>()

            if (data.phone.isBlank() || data.otp.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, mapOf(
                    "error" to "Số điện thoại và OTP không được để trống"
                ))
                return@post
            }

            if (!data.otp.matches(Regex("^\\d{6}$"))) {
                call.respond(HttpStatusCode.BadRequest, mapOf(
                    "error" to "OTP phải là 6 chữ số"
                ))
                return@post
            }

            when (OtpService.verifyAndRemoveOtp(data.phone, data.otp)) {
                VerifyResult.SUCCESS -> {
                    call.respond(HttpStatusCode.OK, VerifyOtpResponse(
                        message = "Xác thực OTP thành công!",
                        phone = data.phone
                    ))
                }
                VerifyResult.INVALID -> {
                    call.respond(HttpStatusCode.BadRequest, mapOf(
                        "error" to "OTP không đúng"
                    ))
                }
                VerifyResult.EXPIRED -> {
                    call.respond(HttpStatusCode.BadRequest, mapOf(
                        "error" to "OTP đã hết hạn"
                    ))
                }
                VerifyResult.NOT_FOUND -> {
                    call.respond(HttpStatusCode.NotFound, mapOf(
                        "error" to "Không tìm thấy OTP. Vui lòng yêu cầu gửi lại"
                    ))
                }
                VerifyResult.TOO_MANY_ATTEMPTS -> {
                    call.respond(HttpStatusCode.TooManyRequests, mapOf(
                        "error" to "Đã vượt quá số lần thử. Vui lòng yêu cầu OTP mới"
                    ))
                }
                VerifyResult.ERROR -> {
                    call.respond(HttpStatusCode.InternalServerError, mapOf(
                        "error" to "Lỗi xác thực OTP"
                    ))
                }
            }
        } catch (e: Exception) {
            println("Lỗi xác thực OTP: ${e.message}")
            e.printStackTrace()
            call.respond(HttpStatusCode.InternalServerError, mapOf(
                "error" to "Lỗi server: ${e.message}"
            ))
        }
    }

    // ✅ ENDPOINT MỚI: Xác thực OTP và trả về Reset Token
    post("/verify_otp_for_reset") {
        try {
            val data = call.receive<VerifyOtpRequest>()

            if (data.phone.isBlank() || data.otp.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, mapOf(
                    "error" to "Số điện thoại và OTP không được để trống"
                ))
                return@post
            }

            if (!data.otp.matches(Regex("^\\d{6}$"))) {
                call.respond(HttpStatusCode.BadRequest, mapOf(
                    "error" to "OTP phải là 6 chữ số"
                ))
                return@post
            }

            when (OtpService.verifyAndRemoveOtp(data.phone, data.otp)) {
                VerifyResult.SUCCESS -> {
                    // ✅ Tạo reset token
                    val resetToken = ResetTokenService.generateToken(data.phone)

                    call.respond(HttpStatusCode.OK, VerifyOtpForResetResponse(
                        message = "Xác thực OTP thành công!",
                        phone = data.phone,
                        reset_token = resetToken,
                        expires_in_minutes = 10
                    ))
                }
                VerifyResult.INVALID -> {
                    call.respond(HttpStatusCode.BadRequest, mapOf(
                        "error" to "OTP không đúng"
                    ))
                }
                VerifyResult.EXPIRED -> {
                    call.respond(HttpStatusCode.BadRequest, mapOf(
                        "error" to "OTP đã hết hạn"
                    ))
                }
                VerifyResult.NOT_FOUND -> {
                    call.respond(HttpStatusCode.NotFound, mapOf(
                        "error" to "Không tìm thấy OTP. Vui lòng yêu cầu gửi lại"
                    ))
                }
                VerifyResult.TOO_MANY_ATTEMPTS -> {
                    call.respond(HttpStatusCode.TooManyRequests, mapOf(
                        "error" to "Đã vượt quá số lần thử. Vui lòng yêu cầu OTP mới"
                    ))
                }
                VerifyResult.ERROR -> {
                    call.respond(HttpStatusCode.InternalServerError, mapOf(
                        "error" to "Lỗi xác thực OTP"
                    ))
                }
            }
        } catch (e: Exception) {
            println("Lỗi xác thực OTP for reset: ${e.message}")
            e.printStackTrace()
            call.respond(HttpStatusCode.InternalServerError, mapOf(
                "error" to "Lỗi server: ${e.message}"
            ))
        }
    }
}

// ============ DTO ============

@kotlinx.serialization.Serializable
data class SendOtpRequest(
    val phone: String
)

@kotlinx.serialization.Serializable
data class SendOtpResponse(
    val message: String,
    val phone: String,
    val expires_in_minutes: Int
)

@kotlinx.serialization.Serializable
data class VerifyOtpRequest(
    val phone: String,
    val otp: String
)

@kotlinx.serialization.Serializable
data class VerifyOtpResponse(
    val message: String,
    val phone: String
)

@kotlinx.serialization.Serializable
data class VerifyOtpForResetResponse(
    val message: String,
    val phone: String,
    val reset_token: String,
    val expires_in_minutes: Int
)