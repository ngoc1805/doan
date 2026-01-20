//package com.example.config
//
//import com.auth0.jwt.JWT
//import com.auth0.jwt.algorithms.Algorithm
//import com.auth0.jwt.exceptions.JWTVerificationException
//import com.auth0.jwt.exceptions.TokenExpiredException
//import com.example.dto.Response.BaseResponse
//import io.ktor.http.auth.*
//import io.ktor.server.application.*
//import io.ktor.server.auth.*
//import io.ktor.server.auth.jwt.*
//import io.ktor.server.response.*
//import io.ktor.http.*
//import io.ktor.server.request.*
//import io.ktor.util.*
//
//// Tạo AttributeKey để lưu lỗi
//val AuthErrorKey = AttributeKey<String>("auth_error")
//
//fun Application.configureSecurity(jwtConfig: JWTConfig) {
//    install(Authentication) {
//        jwt("auth-jwt") {
//            realm = jwtConfig.realm
//
//            // Giữ nguyên verifier mặc định như code cũ
//            verifier(
//                JWT
//                    .require(Algorithm.HMAC256(jwtConfig.secret))
//                    .withAudience(jwtConfig.audience)
//                    .withIssuer(jwtConfig.domain)
//                    .build()
//            )
//
//            authHeader { call ->
//                val accessToken = call.request.cookies["access_token"]
//                if (accessToken.isNullOrBlank()) {
//                    null
//                } else {
//                    HttpAuthHeader.Single("Bearer", accessToken)
//                }
//            }
//
//            validate { credential ->
//                val userId = credential.payload.getClaim("id").asInt()
//                if (userId != null) {
//                    JWTPrincipal(credential.payload)
//                } else {
//                    null
//                }
//            }
//
//            // CHỈ thêm phần challenge để phân biệt lỗi
//            challenge { _, _ ->
//                // Xác định loại lỗi khi bị reject
//                val accessToken = call.request.cookies["access_token"]
//
//                val errorType = if (accessToken.isNullOrBlank()) {
//                    "no_token"
//                } else {
//                    // Kiểm tra token để biết lỗi cụ thể
//                    try {
//                        val verifier = JWT
//                            .require(Algorithm.HMAC256(jwtConfig.secret))
//                            .withAudience(jwtConfig.audience)
//                            .withIssuer(jwtConfig.domain)
//                            .build()
//
//                        verifier.verify(accessToken)
//                        "unknown" // Không nên đến đây vì đã verify rồi
//                    } catch (e: TokenExpiredException) {
//                        "expired"
//                    } catch (e: JWTVerificationException) {
//                        "invalid"
//                    } catch (e: Exception) {
//                        "unknown"
//                    }
//                }
//
//                val message = when (errorType) {
//                    "expired" -> "Token đã hết hạn"
//                    "invalid" -> "Token không hợp lệ"
//                    "no_token" -> "Token không được cung cấp"
//                    else -> "Xác thực thất bại"
//                }
//
//                call.respond(
//                    HttpStatusCode.Unauthorized,
//                    BaseResponse(false, message)
//                )
//            }
//        }
//    }
//}


package com.example.config

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.exceptions.TokenExpiredException
import com.example.dto.Response.BaseResponse
import com.example.repository.RedisTokenRepository
import io.ktor.http.auth.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.util.*

// Tạo AttributeKey để lưu lỗi
val AuthErrorKey = AttributeKey<String>("auth_error")

fun Application.configureSecurity(
    jwtConfig: JWTConfig,
    redisTokenRepository: RedisTokenRepository  //  THÊM THAM SỐ
) {
    install(Authentication) {
        jwt("auth-jwt") {
            realm = jwtConfig.realm

            // Giữ nguyên verifier mặc định như code cũ
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtConfig.secret))
                    .withAudience(jwtConfig.audience)
                    .withIssuer(jwtConfig.domain)
                    .build()
            )
            authHeader { call ->
                val accessToken = call.request.cookies["access_token"]
                if (accessToken.isNullOrBlank()) {
                    null
                } else {
                    HttpAuthHeader.Single("Bearer", accessToken)
                }
            }
            validate { credential ->
                try {
                    //  1. KIỂM TRA BASIC CLAIMS
                    val userId = credential.payload.getClaim("id").asInt()
                    if (userId == null) {
                        println(" JWT validation failed: Missing user ID")
                        return@validate null
                    }

                    //  2. KIỂM TRA TOKEN TYPE
                    val tokenType = credential.payload.getClaim("type").asString()
                    if (tokenType != "access") {
                        println(" JWT validation failed: Invalid token type '$tokenType'")
                        return@validate null
                    }

                    //  3. KIỂM TRA BLACKLIST
                    val jti = credential.payload.id
                    if (jti != null && redisTokenRepository.isBlacklisted(jti)) {
                        println(" Access token đã bị blacklist: $jti")
                        return@validate null
                    }
                    //  4. TOKEN HỢP LỆ
                    println(" JWT validated successfully: userId=$userId")
                    JWTPrincipal(credential.payload)

                } catch (e: Exception) {
                    println(" JWT validation error: ${e.message}")
                    null
                }
            }
            // CHỈ thêm phần challenge để phân biệt lỗi
            challenge { _, _ ->
                // Xác định loại lỗi khi bị reject
                val accessToken = call.request.cookies["access_token"]

                val message = if (accessToken.isNullOrBlank()) {
                    "Token không được cung cấp"
                } else {
                    // Kiểm tra token để biết lỗi cụ thể
                    try {
                        val verifier = JWT
                            .require(Algorithm.HMAC256(jwtConfig.secret))
                            .withAudience(jwtConfig.audience)
                            .withIssuer(jwtConfig.domain)
                            .build()

                        val decoded = verifier.verify(accessToken)
                        val jti = decoded.id

                        //  Kiểm tra blacklist trong challenge
                        if (jti != null && redisTokenRepository.isBlacklisted(jti)) {
                            "Token đã bị thu hồi. Vui lòng đăng nhập lại"
                        } else {
                            "Xác thực thất bại"
                        }
                    } catch (e: TokenExpiredException) {
                        "Token đã hết hạn"
                    } catch (e: JWTVerificationException) {
                        "Token không hợp lệ"
                    } catch (e: Exception) {
                        "Xác thực thất bại"
                    }
                }

                call.respond(
                    HttpStatusCode.Unauthorized,
                    BaseResponse(success = false, message = message)
                )
            }
        }
    }
}