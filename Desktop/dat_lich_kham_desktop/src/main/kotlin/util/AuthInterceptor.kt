package util

import kotlinx.coroutines.runBlocking
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File

class AuthInterceptor(
    private val baseUrl: String,
    private val cookieJar: CookieJarImpl
) : Interceptor {

    private val tokenFile = File("access_token.txt")
    private val refreshTokenFile = File("refresh_token.txt")
    private val userInfoFile = File("user_info.json")

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Bỏ qua các endpoint không cần auth
        if (shouldSkipAuth(originalRequest.url.encodedPath)) {
            return chain.proceed(originalRequest)
        }

        // Thực hiện request ban đầu
        val response = chain.proceed(originalRequest)

        // Nếu 401 Unauthorized -> thử refresh token
        if (response.code == 401) {
            response.close()

            println("[AuthInterceptor] Got 401, attempting refresh...")

            // Kiểm tra có refresh token không
            val hasRefreshToken = refreshTokenFile.exists() && refreshTokenFile.readText().isNotBlank()

            if (!hasRefreshToken) {
                println("[AuthInterceptor] No refresh token, logout required")
                logout()
                return response
            }

            // Gọi API refresh
            val refreshed = runBlocking {
                refreshAccessToken(chain)
            }

            if (refreshed) {
                println("[AuthInterceptor] Refresh successful, retrying original request")
                // Refresh thành công -> thử lại request ban đầu
                val newRequest = originalRequest.newBuilder().build()
                return chain.proceed(newRequest)
            } else {
                println("[AuthInterceptor] Refresh failed, logout required")
                logout()
            }
        }

        return response
    }

    private suspend fun refreshAccessToken(chain: Interceptor.Chain): Boolean {
        return try {
            // Tạo request đến /api/auth/refresh
            val refreshRequest = Request.Builder()
                .url("${baseUrl}api/auth/refresh")
                .post("".toRequestBody("application/json".toMediaType()))
                .build()

            val refreshResponse = chain.proceed(refreshRequest)

            if (refreshResponse.isSuccessful) {
                // Lưu token mới từ Set-Cookie header
                val setCookie = refreshResponse.headers["Set-Cookie"]
                if (setCookie != null && setCookie.contains("access_token=")) {
                    val accessToken = setCookie.substringAfter("access_token=").substringBefore(";")
                    tokenFile.writeText(accessToken)
                    println("[AuthInterceptor] New access token saved")
                }

                refreshResponse.close()
                true
            } else {
                println("[AuthInterceptor] Refresh API returned ${refreshResponse.code}")
                refreshResponse.close()
                false
            }
        } catch (e: Exception) {
            println("[AuthInterceptor] Refresh error: ${e.message}")
            e.printStackTrace()
            false
        }
    }

    private fun shouldSkipAuth(path: String): Boolean {
        val skipPaths = listOf(
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/refresh",
            "/api/auth/send-otp",
            "/api/auth/verify-otp"
        )
        return skipPaths.any { path.contains(it) }
    }

    private fun logout() {
        // Xóa tất cả files
        if (tokenFile.exists()) tokenFile.delete()
        if (refreshTokenFile.exists()) refreshTokenFile.delete()
        if (userInfoFile.exists()) userInfoFile.delete()
        cookieJar.clearAll()
        println("[AuthInterceptor] All tokens cleared")
    }
}