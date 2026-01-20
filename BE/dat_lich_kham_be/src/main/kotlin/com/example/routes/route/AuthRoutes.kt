//package com.example.routes.route
//
//import com.example.dto.Request.LoginRequest
//import com.example.dto.Request.RegisterRequest
//import com.example.dto.Response.LoginResponse
//import com.example.dto.Response.RegisterResponse
//import com.example.service.AuthService
//import com.example.service.LoginResult
//import io.ktor.http.*
//import io.ktor.server.auth.*
//import io.ktor.server.auth.jwt.*
//import io.ktor.server.request.*
//import io.ktor.server.response.*
//import io.ktor.server.routing.*
//import kotlinx.serialization.Serializable
//import kotlinx.serialization.json.Json
//
//fun Route.authRoutes(authService: AuthService) {
//    route("/api/auth") {
//        post("/login") {
//            try {
//                val rawBody = call.receiveText()
//                val loginRequest = Json.decodeFromString<LoginRequest>(rawBody)
//
//                if (loginRequest.username.isBlank() || loginRequest.password.isBlank()) {
//                    call.respond(
//                        HttpStatusCode.BadRequest,
//                        LoginErrorResponse(
//                            message = "Số điện thoại và mật khẩu không được để trống"
//                        )
//                    )
//                    return@post
//                }
//
//                // ✅ GỌI HÀM LOGIN MỚI
//                val result = authService.login(loginRequest)
//
//                when (result) {
//                    is LoginResult.Success -> {
//                        // Lưu ACCESS TOKEN vào cookie
//                        call.response.cookies.append(
//                            Cookie(
//                                "access_token",
//                                result.accessToken,
//                                httpOnly = true,
//                                secure = true,
//                                path = "/",
//                                maxAge = 60 * 60
//                            )
//                        )
//
//                        // Lưu REFRESH TOKEN vào cookie
//                        call.response.cookies.append(
//                            Cookie(
//                                "refresh_token",
//                                result.refreshToken,
//                                httpOnly = true,
//                                secure = true,
//                                path = "/api/auth/refresh",
//                                maxAge = 60 * 60 * 24 * 30
//                            )
//                        )
//
//                        call.respond(
//                            HttpStatusCode.OK,
//                            LoginSuccessResponse(
//                                username = result.username,
//                                message = result.message
//                            )
//                        )
//                    }
//
//                    is LoginResult.Failed -> {
//                        call.respond(
//                            HttpStatusCode.Unauthorized,
//                            LoginErrorResponse(
//                                message = result.message,
//                                attemptsRemaining = result.attemptsRemaining
//                            )
//                        )
//                    }
//
//                    is LoginResult.AccountLocked -> {
//                        call.respond(
//                            HttpStatusCode.Forbidden, // 403
//                            LoginLockedResponse(
//                                message = result.message,
//                                remainingTime = result.remainingTime,
//                                isLocked = true
//                            )
//                        )
//                    }
//                }
//
//            } catch (e: Exception) {
//                call.respond(
//                    HttpStatusCode.BadRequest,
//                    LoginErrorResponse(message = "Lỗi: ${e.message}")
//                )
//            }
//        }
//
//        post("/logout") {
//            // Xóa token khỏi cookie
//            call.response.cookies.append(
//                Cookie(
//                    "access_token",
//                    "",
//                    path = "/",
//                    maxAge = 0
//                )
//            )
//            call.response.cookies.append(
//                Cookie(
//                    "refresh_token",
//                    "",
//                    path = "/api/auth/refresh",
//                    maxAge = 0
//                )
//            )
//            call.respond(HttpStatusCode.OK, "Đã logout thành công!")
//        }
//
////        post("/refresh") {
////            try {
////                val refreshToken = call.request.cookies["refresh_token"]
////
////                if (refreshToken.isNullOrBlank()) {
////                    call.respond(HttpStatusCode.Unauthorized, "Không tìm thấy refresh token")
////                    return@post
////                }
////
////                val userId = authService.verifyRefreshToken(refreshToken)
////
////                if (userId == null) {
////                    call.respond(HttpStatusCode.Unauthorized, "Refresh token không hợp lệ hoặc đã hết hạn")
////                    return@post
////                }
////
////                val account = authService.accountRepository.findById(userId)
////
////                if (account == null) {
////                    call.respond(HttpStatusCode.Unauthorized, "Tài khoản không tồn tại")
////                    return@post
////                }
////
////                val newAccessToken = authService.generateAccessToken(account)
////
////                call.response.cookies.append(
////                    Cookie(
////                        "access_token",
////                        newAccessToken,
////                        httpOnly = true,
////                        secure = true,
////                        path = "/",
////                        maxAge = 60 * 60
////                    )
////                )
////
////                call.respond(HttpStatusCode.OK, mapOf(
////                    "message" to "Đã làm mới token thành công"
////                ))
////
////            } catch (e: Exception) {
////                call.respond(HttpStatusCode.InternalServerError, "Lỗi: ${e.message}")
////            }
////        }
//
//        // File: AuthRoutes.kt - Cập nhật route /refresh
//        post("/refresh") {
//            try {
//                val oldRefreshToken = call.request.cookies["refresh_token"]
//
//                if (oldRefreshToken.isNullOrBlank()) {
//                    call.respond(HttpStatusCode.Unauthorized, mapOf(
//                        "success" to false,
//                        "message" to "Không tìm thấy refresh token"
//                    ))
//                    return@post
//                }
//
//                //  Verify token cũ
//                val userId = authService.verifyRefreshToken(oldRefreshToken)
//
//                if (userId == null) {
//                    call.respond(HttpStatusCode.Unauthorized, mapOf(
//                        "success" to false,
//                        "message" to "Refresh token không hợp lệ hoặc đã hết hạn"
//                    ))
//                    return@post
//                }
//
//                //  Lấy thông tin account
//                val account = authService.accountRepository.findById(userId)
//
//                if (account == null) {
//                    call.respond(HttpStatusCode.Unauthorized, mapOf(
//                        "success" to false,
//                        "message" to "Tài khoản không tồn tại"
//                    ))
//                    return@post
//                }
//
//                //  Tạo CẢ 2 token mới
//                val newAccessToken = authService.generateAccessToken(account)
//                val newRefreshToken = authService.generateRefreshToken(account)
//
//                //  Lưu access token vào cookie
//                call.response.cookies.append(
//                    Cookie(
//                        "access_token",
//                        newAccessToken,
//                        httpOnly = true,
//                        secure = true,
//                        path = "/",
//                        maxAge = 60 * 60  // 1 giờ
//                    )
//                )
//
//                //  Lưu refresh token MỚI vào cookie (ghi đè token cũ)
//                call.response.cookies.append(
//                    Cookie(
//                        "refresh_token",
//                        newRefreshToken,
//                        httpOnly = true,
//                        secure = true,
//                        path = "/api/auth/refresh",
//                        maxAge = 60 * 60 * 24 * 30  // 30 ngày
//                    )
//                )
//
//                call.respond(HttpStatusCode.OK, mapOf(
//                    "success" to true,
//                    "message" to "Đã làm mới cả 2 token thành công"
//                ))
//
//            } catch (e: Exception) {
//                call.respond(HttpStatusCode.InternalServerError, mapOf(
//                    "success" to false,
//                    "message" to "Lỗi: ${e.message}"
//                ))
//            }
//        }
//        //
//
//        post("/register") {
//            try {
//                val rawBody = call.receiveText()
//                val registerRequest = Json.decodeFromString<RegisterRequest>(rawBody)
//                val response = authService.register(registerRequest)
//                if (response.username != null) {
//                    call.respond(HttpStatusCode.OK, response)
//                } else {
//                    call.respond(HttpStatusCode.BadRequest, response)
//                }
//            } catch (e: Exception) {
//                call.respond(HttpStatusCode.BadRequest, RegisterResponse(null, "Lỗi: ${e.message}"))
//            }
//        }
//
//        authenticate("auth-jwt") {
//            get("/profile") {
//                val principal = call.authentication.principal<JWTPrincipal>()
//                if (principal != null) {
//                    val role = principal.payload.getClaim("role").asString()
//                    val allowedRoles = listOf("admin", "benhnhan", "bacsi", "phongchucnang")
//                    if (!allowedRoles.contains(role)) {
//                        call.respond(HttpStatusCode.Forbidden, "Không có quyền truy cập")
//                        return@get
//                    }
//
//                    val profile = mapOf(
//                        "id" to principal.payload.getClaim("id").asInt(),
//                        "username" to principal.payload.getClaim("username").asString(),
//                        "role" to role,
//                        "enabled" to principal.payload.getClaim("enabled").asInt(),
//                        "confirm" to principal.payload.getClaim("confirm").asInt()
//                    )
//                    call.respond(HttpStatusCode.OK, profile)
//                } else {
//                    call.respond(HttpStatusCode.Unauthorized, "Token không hợp lệ")
//                }
//            }
//        }
//    }
//}
//
////  RESPONSE MODELS
//@Serializable
//data class LoginSuccessResponse(
//    val username: String,
//    val message: String
//)
//
//@Serializable
//data class LoginErrorResponse(
//    val message: String,
//    val attemptsRemaining: Int? = null
//)
//
//@Serializable
//data class LoginLockedResponse(
//    val message: String,
//    val remainingTime: Long,
//    val isLocked: Boolean
//)


package com.example.routes.route

import com.example.dto.Request.LoginRequest
import com.example.dto.Request.RegisterRequest
import com.example.dto.Response.RegisterResponse
import com.example.service.AuthService
import com.example.service.LoginResult
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

fun Route.authRoutes(authService: AuthService) {
    route("/api/auth") {
        post("/login") {
            try {
                val rawBody = call.receiveText()
                val loginRequest = Json.decodeFromString<LoginRequest>(rawBody)

                if (loginRequest.username.isBlank() || loginRequest.password.isBlank()) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        LoginErrorResponse(
                            message = "Số điện thoại và mật khẩu không được để trống"
                        )
                    )
                    return@post
                }

                val result = authService.login(loginRequest)

                when (result) {
                    is LoginResult.Success -> {
                        // Lưu ACCESS TOKEN vào cookie
                        call.response.cookies.append(
                            Cookie(
                                "access_token",
                                result.accessToken,
                                httpOnly = true,
                                secure = true,
                                path = "/",
                                maxAge = 60 * 60
                            )
                        )

                        // Lưu REFRESH TOKEN vào cookie
                        call.response.cookies.append(
                            Cookie(
                                "refresh_token",
                                result.refreshToken,
                                httpOnly = true,
                                secure = true,
                                path = "/api/auth/refresh",
                                maxAge = 60 * 60 * 24 * 30
                            )
                        )

                        call.respond(
                            HttpStatusCode.OK,
                            LoginSuccessResponse(
                                username = result.username,
                                message = result.message
                            )
                        )
                    }

                    is LoginResult.Failed -> {
                        call.respond(
                            HttpStatusCode.Unauthorized,
                            LoginErrorResponse(
                                message = result.message,
                                attemptsRemaining = result.attemptsRemaining
                            )
                        )
                    }

                    is LoginResult.AccountLocked -> {
                        call.respond(
                            HttpStatusCode.Forbidden,
                            LoginLockedResponse(
                                message = result.message,
                                remainingTime = result.remainingTime,
                                isLocked = true
                            )
                        )
                    }
                }

            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    LoginErrorResponse(message = "Lỗi: ${e.message}")
                )
            }
        }

        post("/logout") {
            try {
                // Lấy refresh token trước khi xóa
                val refreshToken = call.request.cookies["refresh_token"]

                // REVOKE refresh token (thêm vào blacklist)
                if (!refreshToken.isNullOrBlank()) {
                    authService.revokeRefreshToken(refreshToken)
                }

                // Xóa token khỏi cookie
                call.response.cookies.append(
                    Cookie("access_token", "", path = "/", maxAge = 0)
                )
                call.response.cookies.append(
                    Cookie("refresh_token", "", path = "/api/auth/refresh", maxAge = 0)
                )

                // ✅ FIX: Dùng data class thay vì Map
                call.respond(
                    HttpStatusCode.OK,
                    ApiResponse(
                        success = true,
                        message = "Đã logout thành công!"
                    )
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ApiResponse(
                        success = false,
                        message = "Lỗi khi logout: ${e.message}"
                    )
                )
            }
        }

        post("/refresh") {
            try {
                val oldRefreshToken = call.request.cookies["refresh_token"]

                if (oldRefreshToken.isNullOrBlank()) {
                    call.respond(
                        HttpStatusCode.Unauthorized,
                        ApiResponse(
                            success = false,
                            message = "Không tìm thấy refresh token"
                        )
                    )
                    return@post
                }

                // Verify token (sẽ check blacklist tự động)
                val userId = authService.verifyRefreshToken(oldRefreshToken)

                if (userId == null) {
                    call.respond(
                        HttpStatusCode.Unauthorized,
                        ApiResponse(
                            success = false,
                            message = "Refresh token không hợp lệ hoặc đã hết hạn"
                        )
                    )
                    return@post
                }

                // Lấy thông tin account
                val account = authService.accountRepository.findById(userId)

                if (account == null) {
                    call.respond(
                        HttpStatusCode.Unauthorized,
                        ApiResponse(
                            success = false,
                            message = "Tài khoản không tồn tại"
                        )
                    )
                    return@post
                }

                // REVOKE token cũ (thêm vào blacklist)
                authService.revokeRefreshToken(oldRefreshToken)

                // Tạo CẢ 2 token mới
                val newAccessToken = authService.generateAccessToken(account)
                val newRefreshTokenData = authService.generateRefreshTokenWithJti(account)

                // Lưu refresh token mới vào Redis (whitelist - optional)
                authService.redisTokenRepository.saveUserToken(
                    userId = account.id,
                    tokenJti = newRefreshTokenData.jti,
                    ttlSeconds = authService.jwtConfig.refreshTokenExpiry / 1000
                )

                // Lưu access token vào cookie
                call.response.cookies.append(
                    Cookie(
                        "access_token",
                        newAccessToken,
                        httpOnly = true,
                        secure = true,
                        path = "/",
                        maxAge = 60 * 60
                    )
                )

                // Lưu refresh token MỚI vào cookie
                call.response.cookies.append(
                    Cookie(
                        "refresh_token",
                        newRefreshTokenData.token,
                        httpOnly = true,
                        secure = true,
                        path = "/api/auth/refresh",
                        maxAge = 60 * 60 * 24 * 30
                    )
                )

                // Dùng data class
                call.respond(
                    HttpStatusCode.OK,
                    ApiResponse(
                        success = true,
                        message = "Đã làm mới cả 2 token thành công"
                    )
                )

            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ApiResponse(
                        success = false,
                        message = "Lỗi: ${e.message}"
                    )
                )
            }
        }

        post("/register") {
            try {
                val rawBody = call.receiveText()
                val registerRequest = Json.decodeFromString<RegisterRequest>(rawBody)
                val response = authService.register(registerRequest)
                if (response.username != null) {
                    call.respond(HttpStatusCode.OK, response)
                } else {
                    call.respond(HttpStatusCode.BadRequest, response)
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, RegisterResponse(null, "Lỗi: ${e.message}"))
            }
        }

        authenticate("auth-jwt") {
            get("/profile") {
                val principal = call.authentication.principal<JWTPrincipal>()
                if (principal != null) {
                    val role = principal.payload.getClaim("role").asString()
                    val allowedRoles = listOf("admin", "benhnhan", "bacsi", "phongchucnang")
                    if (!allowedRoles.contains(role)) {
                        call.respond(HttpStatusCode.Forbidden, "Không có quyền truy cập")
                        return@get
                    }

                    val profile = mapOf(
                        "id" to principal.payload.getClaim("id").asInt(),
                        "username" to principal.payload.getClaim("username").asString(),
                        "role" to role,
                        "enabled" to principal.payload.getClaim("enabled").asInt(),
                        "confirm" to principal.payload.getClaim("confirm").asInt()
                    )
                    call.respond(HttpStatusCode.OK, profile)
                } else {
                    call.respond(HttpStatusCode.Unauthorized, "Token không hợp lệ")
                }
            }
        }
    }
}

// ✅ RESPONSE MODELS - THÊM ApiResponse
@Serializable
data class ApiResponse(
    val success: Boolean,
    val message: String
)

@Serializable
data class LoginSuccessResponse(
    val username: String,
    val message: String
)

@Serializable
data class LoginErrorResponse(
    val message: String,
    val attemptsRemaining: Int? = null
)

@Serializable
data class LoginLockedResponse(
    val message: String,
    val remainingTime: Long,
    val isLocked: Boolean
)