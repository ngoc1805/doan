////// File: AuthInterceptor.kt - CHỈ REFRESH KHI "Token đã hết hạn"
////package com.example.dat_lich_kham_fe.util
////
////import android.content.Context
////import com.example.dat_lich_kham_fe.data.api.refresh
////import com.example.dat_lich_kham_fe.data.model.BaseResponse
////import com.google.gson.Gson
////import okhttp3.Interceptor
////import okhttp3.MediaType.Companion.toMediaTypeOrNull
////import okhttp3.Request
////import okhttp3.Response
////
////class AuthInterceptor(
////    private val context: Context,
////    private val cookieJar: PersistentCookieJar
////) : Interceptor {
////
////    private val gson = Gson()
////
////    override fun intercept(chain: Interceptor.Chain): Response {
////        val originalRequest = chain.request()
////
////        // Bỏ qua các endpoint không cần auth
////        if (shouldSkipAuth(originalRequest.url.encodedPath)) {
////            return chain.proceed(originalRequest)
////        }
////
////        // Thực hiện request
////        val response = chain.proceed(originalRequest)
////
////        // Nếu 401 Unauthorized -> kiểm tra message
////        if (response.code == 401) {
////            println("[INTERCEPT] Got 401, checking message...")
////
////            val responseBody = response.peekBody(Long.MAX_VALUE).string()
////
////            try {
////                val baseResponse = gson.fromJson(responseBody, BaseResponse::class.java)
////                val message = baseResponse.message
////
////                println("[INTERCEPT] Message: $message")
////
////                // CHỈ refresh khi message là "Token đã hết hạn"
////                if (message.contains("hết hạn", ignoreCase = true) ||
////                    message.contains("expired", ignoreCase = true)) {
////                    println("[INTERCEPT] Token expired, attempting refresh")
////                    response.close()
////
////                    val hasRefreshToken = cookieJar.getRefreshToken() != null
////
////                    if (!hasRefreshToken) {
////                        println("[INTERCEPT] No refresh token, cannot refresh")
////                        return buildUnauthorizedResponse(originalRequest, message)
////                    }
////
////                    val refreshed = refreshAccessToken(chain)
////
////                    if (refreshed) {
////                        println("[INTERCEPT] Refresh success, retrying request")
////                        val newRequest = originalRequest.newBuilder().build()
////                        return chain.proceed(newRequest)
////                    } else {
////                        println("[INTERCEPT] Refresh failed")
////                        return buildUnauthorizedResponse(originalRequest, message)
////                    }
////                } else {
////                    // Các lỗi khác (token không hợp lệ, không được cung cấp)
////                    // -> Không làm gì, chỉ return response gốc
////                    println("[INTERCEPT] Other auth error: $message")
////                    return response
////                }
////            } catch (e: Exception) {
////                e.printStackTrace()
////                println("[INTERCEPT] Error parsing response")
////                return response
////            }
////        }
////
////        return response
////    }
////
////    private fun refreshAccessToken(chain: Interceptor.Chain): Boolean {
////        return try {
////            val refreshRequest = Request.Builder()
////                .url(refresh)
////                .post(okhttp3.RequestBody.create(null, ByteArray(0)))
////                .build()
////
////            val refreshResponse = chain.proceed(refreshRequest)
////
////            if (refreshResponse.isSuccessful) {
////                println("[INTERCEPT] Refresh successful")
////                refreshResponse.close()
////                true
////            } else {
////                println("[INTERCEPT] Refresh failed with code ${refreshResponse.code}")
////                refreshResponse.close()
////                false
////            }
////        } catch (e: Exception) {
////            e.printStackTrace()
////            false
////        }
////    }
////
////    private fun shouldSkipAuth(path: String): Boolean {
////        val skipPaths = listOf(
////            "/api/auth/login",
////            "/api/auth/register",
////            "/api/auth/refresh",
////            "/api/auth/send-otp",
////            "/api/auth/verify-otp"
////        )
////        return skipPaths.any { path.contains(it) }
////    }
////
////    private fun buildUnauthorizedResponse(request: Request, message: String): Response {
////        return Response.Builder()
////            .request(request)
////            .protocol(okhttp3.Protocol.HTTP_1_1)
////            .code(401)
////            .message("Unauthorized")
////            .body(okhttp3.ResponseBody.create(
////                "application/json".toMediaTypeOrNull(),
////                """{"success":false,"message":"$message"}"""
////            ))
////            .build()
////    }
////}
//
////---------
//// File: AuthInterceptor.kt - Cập nhật xử lý refresh với rotation
//package com.example.nhom1_fe_datnhahang.util
//
//import android.content.Context
//import com.example.nhom1_fe_datnhahang.data.api.refresh
//import com.example.nhom1_fe_datnhahang.data.model.BaseResponse
//import com.google.gson.Gson
//import okhttp3.Interceptor
//import okhttp3.MediaType.Companion.toMediaTypeOrNull
//import okhttp3.Request
//import okhttp3.Response
//
//class AuthInterceptor(
//    private val context: Context,
//    private val cookieJar: PersistentCookieJar
//) : Interceptor {
//
//    private val gson = Gson()
//
//    override fun intercept(chain: Interceptor.Chain): Response {
//        val originalRequest = chain.request()
//
//        // Bỏ qua các endpoint không cần auth
//        if (shouldSkipAuth(originalRequest.url.encodedPath)) {
//            return chain.proceed(originalRequest)
//        }
//
//        // Thực hiện request
//        val response = chain.proceed(originalRequest)
//
//        // Nếu 401 Unauthorized -> kiểm tra message
//        if (response.code == 401) {
//            println("[INTERCEPT] Got 401, checking message...")
//
//            val responseBody = response.peekBody(Long.MAX_VALUE).string()
//
//            try {
//                val baseResponse = gson.fromJson(responseBody, BaseResponse::class.java)
//                val message = baseResponse.message
//
//                println("[INTERCEPT] Message: $message")
//
//                // CHỈ refresh khi message là "Token đã hết hạn"
//                if (message.contains("hết hạn", ignoreCase = true) ||
//                    message.contains("expired", ignoreCase = true)) {
//                    println("[INTERCEPT] Token expired, attempting refresh")
//                    response.close()
//
//                    val hasRefreshToken = cookieJar.getRefreshToken() != null
//
//                    if (!hasRefreshToken) {
//                        println("[INTERCEPT] No refresh token, cannot refresh")
//                        return buildUnauthorizedResponse(originalRequest, message)
//                    }
//
//                    //  Gọi refresh (backend sẽ tự động cấp cả 2 token mới)
//                    val refreshed = refreshAccessToken(chain)
//
//                    if (refreshed) {
//                        println("[INTERCEPT] Refresh success, retrying request")
//                        val newRequest = originalRequest.newBuilder().build()
//                        return chain.proceed(newRequest)
//                    } else {
//                        println("[INTERCEPT] Refresh failed")
//                        return buildUnauthorizedResponse(originalRequest, message)
//                    }
//                } else {
//                    // Các lỗi khác (token không hợp lệ, không được cung cấp)
//                    println("[INTERCEPT] Other auth error: $message")
//                    return response
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//                println("[INTERCEPT] Error parsing response")
//                return response
//            }
//        }
//        return response
//    }
//
//    private fun refreshAccessToken(chain: Interceptor.Chain): Boolean {
//        return try {
//            val refreshRequest = Request.Builder()
//                .url(refresh)
//                .post(okhttp3.RequestBody.create(null, ByteArray(0)))
//                .build()
//
//            println("[INTERCEPT] Sending refresh request...")
//            val refreshResponse = chain.proceed(refreshRequest)
//
//            if (refreshResponse.isSuccessful) {
//                println("[INTERCEPT]  Refresh successful")
//                println("[INTERCEPT]  CookieJar đã tự động lưu 2 token mới từ Set-Cookie header")
//
//                // Debug: In ra cookies mới (optional)
//                val newAccessToken = cookieJar.getAccessToken()
//                val newRefreshToken = cookieJar.getRefreshToken()
//                println("[INTERCEPT] New Access Token: ${newAccessToken?.take(20)}...")
//                println("[INTERCEPT] New Refresh Token: ${newRefreshToken?.take(20)}...")
//
//                refreshResponse.close()
//                true
//            } else {
//                println("[INTERCEPT]  Refresh failed with code ${refreshResponse.code}")
//                refreshResponse.close()
//                false
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            println("[INTERCEPT]  Refresh exception: ${e.message}")
//            false
//        }
//    }
//
//    private fun shouldSkipAuth(path: String): Boolean {
//        val skipPaths = listOf(
//            "/api/auth/login",
//            "/api/auth/register",
//            "/api/auth/refresh",
//            "/api/auth/send-otp",
//            "/api/auth/verify-otp"
//        )
//        return skipPaths.any { path.contains(it) }
//    }
//
//    private fun buildUnauthorizedResponse(request: Request, message: String): Response {
//        return Response.Builder()
//            .request(request)
//            .protocol(okhttp3.Protocol.HTTP_1_1)
//            .code(401)
//            .message("Unauthorized")
//            .body(okhttp3.ResponseBody.create(
//                "application/json".toMediaTypeOrNull(),
//                """{"success":false,"message":"$message"}"""
//            ))
//            .build()
//    }
//}