//package com.example.service
//
//import com.auth0.jwt.JWT
//import com.auth0.jwt.algorithms.Algorithm
//import com.example.config.JWTConfig
//import com.example.dto.Request.LoginRequest
//import com.example.dto.Request.RegisterRequest
//import com.example.dto.Response.LoginResponse
//import com.example.dto.Response.RegisterResponse
//import com.example.models.*
//import com.example.repository.AccountRepository
//import java.util.*
//
//class AuthService(
//    val accountRepository: AccountRepository,
//    private val jwtConfig: JWTConfig,
//    private val securityService: LoginSecurityService  // ✅ THÊM SERVICE BẢO MẬT
//) {
//
//    suspend fun login(loginRequest: LoginRequest): LoginResult {
//        try {
//            val username = loginRequest.username
//
//            // ✅ 1. KIỂM TRA TÀI KHOẢN CÓ BỊ KHÓA KHÔNG
//            val (isLocked, remainingTime) = securityService.isAccountLocked(username)
//            if (isLocked) {
//                val timeText = securityService.formatRemainingTime(remainingTime)
//                return LoginResult.AccountLocked(
//                    message = "Tài khoản bị khóa do đăng nhập sai quá nhiều lần. Vui lòng thử lại sau $timeText",
//                    remainingTime = remainingTime
//                )
//            }
//
//            // ✅ 2. KIỂM TRA TÀI KHOẢN TỒN TẠI
//            val account = accountRepository.findByPhone(username)
//            if (account == null) {
//                // Ghi nhận thất bại (nhưng không hiển thị chi tiết)
//                securityService.recordFailedAttempt(username)
//                return LoginResult.Failed(
//                    message = "Số điện thoại hoặc mật khẩu không chính xác"
//                )
//            }
//
//            // ✅ 3. KIỂM TRA MẬT KHẨU
//            val isValidPassword = accountRepository.validatePassword(username, loginRequest.password)
//            if (!isValidPassword) {
//                // Ghi nhận lần đăng nhập sai
//                val securityResult = securityService.recordFailedAttempt(username)
//
//                return when (securityResult) {
//                    is LoginSecurityResult.FailedAttempt -> {
//                        LoginResult.Failed(
//                            message = "Mật khẩu không chính xác. Còn ${securityResult.remainingAttempts} lần thử.",
//                            attemptsRemaining = securityResult.remainingAttempts
//                        )
//                    }
//                    is LoginSecurityResult.AccountLocked -> {
//                        val timeText = securityService.formatRemainingTime(securityResult.remainingTime)
//                        LoginResult.AccountLocked(
//                            message = "Tài khoản đã bị khóa do đăng nhập sai ${securityResult.attemptsUsed} lần. Vui lòng thử lại sau $timeText",
//                            remainingTime = securityResult.remainingTime
//                        )
//                    }
//                    else -> LoginResult.Failed(message = "Mật khẩu không chính xác")
//                }
//            }
//
//            // ✅ 4. KIỂM TRA TÀI KHOẢN CÓ ENABLED KHÔNG
//            if (account.enabled.toInt() == 0) {
//                return LoginResult.Failed(message = "Tài khoản đã bị vô hiệu hóa")
//            }
//
//            // ✅ 5. ĐĂNG NHẬP THÀNH CÔNG -> RESET BẢO MẬT
//            securityService.recordSuccessfulLogin(username)
//
//            // Tạo tokens
//            val accessToken = generateAccessToken(account)
//            val refreshToken = generateRefreshToken(account)
//
//            return LoginResult.Success(
//                username = account.username,
//                message = "Đăng nhập thành công",
//                accessToken = accessToken,
//                refreshToken = refreshToken
//            )
//
//        } catch (e: Exception) {
//            return LoginResult.Failed(message = "Lỗi hệ thống: ${e.message}")
//        }
//    }
//
//    // Các hàm khác giữ nguyên
//    fun generateAccessToken(account: Account): String {
//        return JWT.create()
//            .withAudience(jwtConfig.audience)
//            .withIssuer(jwtConfig.domain)
//            .withClaim("id", account.id)
//            .withClaim("username", account.username)
//            .withClaim("role", account.role ?: "")
//            .withClaim("enabled", account.enabled.toInt())
//            .withClaim("confirm", account.confirm.toInt())
//            .withClaim("type", "access")
//            .withExpiresAt(Date(System.currentTimeMillis() + jwtConfig.accessTokenExpiry))
//            .sign(Algorithm.HMAC256(jwtConfig.secret))
//    }
//
//    fun generateRefreshToken(account: Account): String {
//        return JWT.create()
//            .withAudience(jwtConfig.audience)
//            .withIssuer(jwtConfig.domain)
//            .withClaim("id", account.id)
//            .withClaim("type", "refresh")
//            .withExpiresAt(Date(System.currentTimeMillis() + jwtConfig.refreshTokenExpiry))
//            .sign(Algorithm.HMAC256(jwtConfig.secret))
//    }
//
//    fun verifyRefreshToken(token: String): Int? {
//        return try {
//            val verifier = JWT.require(Algorithm.HMAC256(jwtConfig.secret))
//                .withAudience(jwtConfig.audience)
//                .withIssuer(jwtConfig.domain)
//                .build()
//
//            val decoded = verifier.verify(token)
//            val type = decoded.getClaim("type").asString()
//
//            if (type == "refresh") {
//                decoded.getClaim("id").asInt()
//            } else {
//                null
//            }
//        } catch (e: Exception) {
//            println("Refresh token invalid: ${e.message}")
//            null
//        }
//    }
//
//    suspend fun register(request: RegisterRequest): RegisterResponse {
//        if (request.username.isBlank() || request.password.isBlank()) {
//            return RegisterResponse(null, "Số điện thoại và mật khẩu không được để trống")
//        }
//        val success = accountRepository.createAccount(request.username, request.password, request.roleId)
//        return if (success) {
//            RegisterResponse(request.username, "Đăng ký thành công")
//        } else {
//            RegisterResponse(null, "Số điện thoại đã tồn tại")
//        }
//    }
//}
//
//// ✅ THÊM SEALED CLASS ĐỂ XỬ LÝ KẾT QUẢ
//sealed class LoginResult {
//    data class Success(
//        val username: String,
//        val message: String,
//        val accessToken: String,
//        val refreshToken: String
//    ) : LoginResult()
//
//    data class Failed(
//        val message: String,
//        val attemptsRemaining: Int? = null
//    ) : LoginResult()
//
//    data class AccountLocked(
//        val message: String,
//        val remainingTime: Long
//    ) : LoginResult()
//}

package com.example.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.config.JWTConfig
import com.example.dto.Request.LoginRequest
import com.example.dto.Request.RegisterRequest
import com.example.dto.Response.RegisterResponse
import com.example.models.*
import com.example.repository.AccountRepository
import com.example.repository.RedisTokenRepository
import java.util.*

class AuthService(
    val accountRepository: AccountRepository,
    val jwtConfig: JWTConfig,
    private val securityService: LoginSecurityService,
    val redisTokenRepository: RedisTokenRepository  //  Thêm Redis
) {

    suspend fun login(loginRequest: LoginRequest): LoginResult {
        try {
            val username = loginRequest.username

            //  1. KIỂM TRA TÀI KHOẢN CÓ BỊ KHÓA KHÔNG
            val (isLocked, remainingTime) = securityService.isAccountLocked(username)
            if (isLocked) {
                val timeText = securityService.formatRemainingTime(remainingTime)
                return LoginResult.AccountLocked(
                    message = "Tài khoản bị khóa do đăng nhập sai quá nhiều lần. Vui lòng thử lại sau $timeText",
                    remainingTime = remainingTime
                )
            }

            //  2. KIỂM TRA TÀI KHOẢN TỒN TẠI
            val account = accountRepository.findByPhone(username)
            if (account == null) {
                securityService.recordFailedAttempt(username)
                return LoginResult.Failed(
                    message = "Số điện thoại hoặc mật khẩu không chính xác"
                )
            }

            //  3. KIỂM TRA MẬT KHẨU
            val isValidPassword = accountRepository.validatePassword(username, loginRequest.password)
            if (!isValidPassword) {
                val securityResult = securityService.recordFailedAttempt(username)

                return when (securityResult) {
                    is LoginSecurityResult.FailedAttempt -> {
                        LoginResult.Failed(
                            message = "Mật khẩu không chính xác. Còn ${securityResult.remainingAttempts} lần thử.",
                            attemptsRemaining = securityResult.remainingAttempts
                        )
                    }
                    is LoginSecurityResult.AccountLocked -> {
                        val timeText = securityService.formatRemainingTime(securityResult.remainingTime)
                        LoginResult.AccountLocked(
                            message = "Tài khoản đã bị khóa do đăng nhập sai ${securityResult.attemptsUsed} lần. Vui lòng thử lại sau $timeText",
                            remainingTime = securityResult.remainingTime
                        )
                    }
                    else -> LoginResult.Failed(message = "Mật khẩu không chính xác")
                }
            }

            //  4. KIỂM TRA TÀI KHOẢN CÓ ENABLED KHÔNG
            if (account.enabled.toInt() == 0) {
                return LoginResult.Failed(message = "Tài khoản đã bị vô hiệu hóa")
            }

            //  5. ĐĂNG NHẬP THÀNH CÔNG -> RESET BẢO MẬT
            securityService.recordSuccessfulLogin(username)

            //  6. TẠO TOKENS VỚI JTI
            val accessToken = generateAccessToken(account)
            val refreshTokenData = generateRefreshTokenWithJti(account)

            //  7. LƯU REFRESH TOKEN VÀO REDIS (WHITELIST - OPTIONAL)
            // Chỉ cần nếu bạn muốn track active tokens
            redisTokenRepository.saveUserToken(
                userId = account.id,
                tokenJti = refreshTokenData.jti,
                ttlSeconds = jwtConfig.refreshTokenExpiry / 1000
            )

            return LoginResult.Success(
                username = account.username,
                message = "Đăng nhập thành công",
                accessToken = accessToken,
                refreshToken = refreshTokenData.token
            )

        } catch (e: Exception) {
            return LoginResult.Failed(message = "Lỗi hệ thống: ${e.message}")
        }
    }

    //  Generate Access Token (không cần JTI vì short-lived)
    fun generateAccessToken(account: Account): String {
        return JWT.create()
            .withAudience(jwtConfig.audience)
            .withIssuer(jwtConfig.domain)
            .withClaim("id", account.id)
            .withClaim("username", account.username)
            .withClaim("role", account.role ?: "")
            .withClaim("enabled", account.enabled.toInt())
            .withClaim("confirm", account.confirm.toInt())
            .withClaim("type", "access")
            .withExpiresAt(Date(System.currentTimeMillis() + jwtConfig.accessTokenExpiry))
            .sign(Algorithm.HMAC256(jwtConfig.secret))
    }

    //  Generate Refresh Token VỚI JTI
    data class RefreshTokenData(val token: String, val jti: String, val expiresAt: Date)

    fun generateRefreshTokenWithJti(account: Account): RefreshTokenData {
        val jti = UUID.randomUUID().toString()
        val expiresAt = Date(System.currentTimeMillis() + jwtConfig.refreshTokenExpiry)

        val token = JWT.create()
            .withAudience(jwtConfig.audience)
            .withIssuer(jwtConfig.domain)
            .withClaim("id", account.id)
            .withClaim("type", "refresh")
            .withJWTId(jti)  //  Thêm JTI
            .withExpiresAt(expiresAt)
            .sign(Algorithm.HMAC256(jwtConfig.secret))

        return RefreshTokenData(token, jti, expiresAt)
    }

    //  Verify Refresh Token VỚI BLACKLIST CHECK
    suspend fun verifyRefreshToken(token: String): Int? {
        return try {
            val verifier = JWT.require(Algorithm.HMAC256(jwtConfig.secret))
                .withAudience(jwtConfig.audience)
                .withIssuer(jwtConfig.domain)
                .build()

            val decoded = verifier.verify(token)
            val type = decoded.getClaim("type").asString()
            val jti = decoded.id  //  Lấy JTI

            if (type != "refresh") {
                println(" Token không phải refresh type")
                return null
            }

            //  KIỂM TRA BLACKLIST
            if (redisTokenRepository.isBlacklisted(jti)) {
                println(" Token đã bị revoke: $jti")
                return null
            }

            decoded.getClaim("id").asInt()

        } catch (e: Exception) {
            println(" Refresh token invalid: ${e.message}")
            null
        }
    }

    //  Revoke Refresh Token (thêm vào blacklist)
    suspend fun revokeRefreshToken(token: String): Boolean {
        return try {
            val decoded = JWT.decode(token)
            val jti = decoded.id
            val expiresAt = decoded.expiresAt
            val userId = decoded.getClaim("id").asInt()

            // Tính TTL còn lại
            val ttlSeconds = (expiresAt.time - System.currentTimeMillis()) / 1000

            if (ttlSeconds > 0) {
                //  Thêm vào blacklist
                redisTokenRepository.addToBlacklist(jti, ttlSeconds)

                // Xóa khỏi user tokens (nếu dùng whitelist)
                // redisTokenRepository.removeUserToken(userId, jti)

                println(" Token revoked: $jti (TTL: ${ttlSeconds}s)")
                true
            } else {
                println("️ Token đã hết hạn, không cần revoke")
                false
            }
        } catch (e: Exception) {
            println(" Error revoking refresh token: ${e.message}")
            false
        }
    }

    //  Revoke tất cả token của user (khi đổi mật khẩu)
    suspend fun revokeAllUserTokens(userId: Int): Int {
        return redisTokenRepository.blacklistAllUserTokens(
            userId = userId,
            ttlSeconds = jwtConfig.refreshTokenExpiry / 1000
        )
    }

    suspend fun register(request: RegisterRequest): RegisterResponse {
        if (request.username.isBlank() || request.password.isBlank()) {
            return RegisterResponse(null, "Số điện thoại và mật khẩu không được để trống")
        }
        val success = accountRepository.createAccount(request.username, request.password, request.roleId)
        return if (success) {
            RegisterResponse(request.username, "Đăng ký thành công")
        } else {
            RegisterResponse(null, "Số điện thoại đã tồn tại")
        }
    }
}

//  SEALED CLASS ĐỂ XỬ LÝ KẾT QUẢ
sealed class LoginResult {
    data class Success(
        val username: String,
        val message: String,
        val accessToken: String,
        val refreshToken: String
    ) : LoginResult()

    data class Failed(
        val message: String,
        val attemptsRemaining: Int? = null
    ) : LoginResult()

    data class AccountLocked(
        val message: String,
        val remainingTime: Long
    ) : LoginResult()
}