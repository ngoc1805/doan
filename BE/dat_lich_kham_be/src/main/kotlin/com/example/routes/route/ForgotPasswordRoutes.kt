package com.example.routes.route

import com.example.repository.AccountRepository
import com.example.service.ResetTokenService
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*

fun Route.forgotPasswordRoutes(accountRepository: AccountRepository) {

    /**
     * Endpoint: Reset mật khẩu với token
     * POST /reset_password
     * Body: {
     *   "reset_token": "uuid-string",
     *   "new_password": "NewPass123"
     * }
     */
    post("/reset_password") {
        try {
            val data = call.receive<ResetPasswordRequest>()

            // Validate input
            if (data.reset_token.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, mapOf(
                    "error" to "Token không được để trống"
                ))
                return@post
            }

            if (data.new_password.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, mapOf(
                    "error" to "Mật khẩu mới không được để trống"
                ))
                return@post
            }

            // Validate password length
            if (data.new_password.length < 6) {
                call.respond(HttpStatusCode.BadRequest, mapOf(
                    "error" to "Mật khẩu phải có ít nhất 6 ký tự"
                ))
                return@post
            }

            //  Validate token và lấy phone number
            val phone = ResetTokenService.validateAndConsumeToken(data.reset_token)

            if (phone == null) {
                call.respond(HttpStatusCode.Unauthorized, mapOf(
                    "error" to "Token không hợp lệ hoặc đã hết hạn. Vui lòng thực hiện lại quy trình quên mật khẩu"
                ))
                return@post
            }

            //  Reset password
            val success = accountRepository.resetPasswordWithToken(phone, data.new_password)

            if (success) {
                call.respond(HttpStatusCode.OK, ResetPasswordResponse(
                    message = "Đổi mật khẩu thành công! Vui lòng đăng nhập lại",
                    phone = phone
                ))
            } else {
                call.respond(HttpStatusCode.InternalServerError, mapOf(
                    "error" to "Không thể đổi mật khẩu. Vui lòng thử lại"
                ))
            }
        } catch (e: Exception) {
            println("Lỗi reset password: ${e.message}")
            e.printStackTrace()
            call.respond(HttpStatusCode.InternalServerError, mapOf(
                "error" to "Lỗi server: ${e.message}"
            ))
        }
    }
}

// ============ DTO ============

@kotlinx.serialization.Serializable
data class ResetPasswordRequest(
    val reset_token: String,
    val new_password: String
)

@kotlinx.serialization.Serializable
data class ResetPasswordResponse(
    val message: String,
    val phone: String
)