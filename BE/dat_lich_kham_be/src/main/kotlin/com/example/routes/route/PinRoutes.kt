package com.example.routes.route

import com.example.repository.AccountRepository
import com.example.service.ResetTokenService
import com.example.service.UserService
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*

fun Route.pinRoutes(
    accountRepository: AccountRepository,
    userService: UserService
) {

    /**
     * Endpoint: Reset PIN với token (sau khi verify OTP)
     * POST /reset_pin
     * Body: {
     *   "reset_token": "uuid-string",
     *   "new_pin": "123456"
     * }
     */
    post("/reset_pin") {
        try {
            val data = call.receive<ResetPinRequest>()

            // Validate input
            if (data.reset_token.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, mapOf(
                    "error" to "Token không được để trống"
                ))
                return@post
            }

            if (data.new_pin.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, mapOf(
                    "error" to "Mã PIN mới không được để trống"
                ))
                return@post
            }

            // Validate PIN format (6 chữ số)
            if (!data.new_pin.matches(Regex("^\\d{6}$"))) {
                call.respond(HttpStatusCode.BadRequest, mapOf(
                    "error" to "Mã PIN phải là 6 chữ số"
                ))
                return@post
            }

            //  Validate token và lấy phone number
            val phone = ResetTokenService.validateAndConsumeToken(data.reset_token)

            if (phone == null) {
                call.respond(HttpStatusCode.Unauthorized, mapOf(
                    "error" to "Token không hợp lệ hoặc đã hết hạn. Vui lòng thực hiện lại quy trình quên mã PIN"
                ))
                return@post
            }

            // Lấy accountId từ phone
            val account = accountRepository.findByPhone(phone)
            if (account == null) {
                call.respond(HttpStatusCode.NotFound, mapOf(
                    "error" to "Không tìm thấy tài khoản"
                ))
                return@post
            }

            //  Reset PIN
            val success = userService.resetPinWithToken(account.id, data.new_pin)

            if (success) {
                call.respond(HttpStatusCode.OK, ResetPinResponse(
                    message = "Đổi mã PIN thành công!",
                    phone = phone
                ))
            } else {
                call.respond(HttpStatusCode.InternalServerError, mapOf(
                    "error" to "Không thể đổi mã PIN. Vui lòng thử lại"
                ))
            }
        } catch (e: Exception) {
            println("Lỗi reset PIN: ${e.message}")
            e.printStackTrace()
            call.respond(HttpStatusCode.InternalServerError, mapOf(
                "error" to "Lỗi server: ${e.message}"
            ))
        }
    }

    /**
     * Endpoint: Đổi PIN (cần PIN cũ)
     * POST /change_pin
     * Body: {
     *   "user_id": 123,
     *   "old_pin": "123456",
     *   "new_pin": "654321"
     * }
     */
    post("/change_pin") {
        try {
            val data = call.receive<ChangePinRequest>()

            // Validate input
            if (data.old_pin.isBlank() || data.new_pin.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, mapOf(
                    "error" to "Mã PIN cũ và mã PIN mới không được để trống"
                ))
                return@post
            }

            // Validate PIN format
            if (!data.old_pin.matches(Regex("^\\d{6}$")) || !data.new_pin.matches(Regex("^\\d{6}$"))) {
                call.respond(HttpStatusCode.BadRequest, mapOf(
                    "error" to "Mã PIN phải là 6 chữ số"
                ))
                return@post
            }

            if (data.old_pin == data.new_pin) {
                call.respond(HttpStatusCode.BadRequest, mapOf(
                    "error" to "Mã PIN mới phải khác mã PIN cũ"
                ))
                return@post
            }

            //  Đổi PIN
            val success = userService.changePinWithOldPin(data.user_id, data.old_pin, data.new_pin)

            if (success) {
                call.respond(HttpStatusCode.OK, ChangePinResponse(
                    message = "Đổi mã PIN thành công!"
                ))
            } else {
                call.respond(HttpStatusCode.BadRequest, mapOf(
                    "error" to "Mã PIN cũ không đúng hoặc không tìm thấy người dùng"
                ))
            }
        } catch (e: Exception) {
            println("Lỗi change PIN: ${e.message}")
            e.printStackTrace()
            call.respond(HttpStatusCode.InternalServerError, mapOf(
                "error" to "Lỗi server: ${e.message}"
            ))
        }
    }
}

// ============ DTO ============

@kotlinx.serialization.Serializable
data class ResetPinRequest(
    val reset_token: String,
    val new_pin: String
)

@kotlinx.serialization.Serializable
data class ResetPinResponse(
    val message: String,
    val phone: String
)

@kotlinx.serialization.Serializable
data class ChangePinRequest(
    val user_id: Int,
    val old_pin: String,
    val new_pin: String
)

@kotlinx.serialization.Serializable
data class ChangePinResponse(
    val message: String
)