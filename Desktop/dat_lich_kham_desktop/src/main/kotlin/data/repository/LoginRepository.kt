//package data.repository
//
//import data.api.RetrofitInstance
//import data.model.LoginRequest
//import data.model.LoginResponse
//import data.model.AccountInfo
//import util.JwtUtils
//import retrofit2.Response
//import java.io.File
//import org.json.JSONObject
//
//class LoginRepository {
//
//    private val tokenFile = File("access_token.txt")
//    private val userInfoFile = File("user_info.json")
//
//    suspend fun login(username: String, password: String): LoginResponse {
//        val response: Response<LoginResponse> = RetrofitInstance.loginApi.login(LoginRequest(username, password))
//
//        if (response.isSuccessful) {
//            val setCookie = response.headers()["Set-Cookie"]
//            if (setCookie != null && setCookie.contains("access_token=")) {
//                val accessToken = setCookie.substringAfter("access_token=").substringBefore(";")
//                tokenFile.writeText(accessToken)
//
//                // Lưu info ra file user_info.json
//                val info = JwtUtils.parseAccountInfo(accessToken)
//                if (info != null) {
//                    val json = JSONObject()
//                    json.put("accountId", info.accountId)
//                    json.put("username", info.username)
//                    json.put("role", info.role)
//                    userInfoFile.writeText(json.toString())
//                }
//            }
//            return response.body() ?: LoginResponse(null, "Lỗi không có dữ liệu")
//        } else {
//            return LoginResponse(null, "Đăng nhập thất bại")
//        }
//    }
//
//    fun getAccessToken(): String? {
//        return if (tokenFile.exists()) tokenFile.readText() else null
//    }
//
//    fun getUserInfo(): AccountInfo? {
//        if (userInfoFile.exists()) {
//            val json = JSONObject(userInfoFile.readText())
//            val accountId = json.optInt("accountId", -1)
//            val username = json.optString("username", "")
//            val role = json.optString("role", "")
//            return AccountInfo(accountId, username, role)
//        }
//        return null
//    }
//
//    fun logout() {
//        if (tokenFile.exists()) tokenFile.delete()
//        if (userInfoFile.exists()) userInfoFile.delete()
//    }
//
//}

package data.repository

import data.api.RetrofitInstance
import data.model.LoginRequest
import data.model.LoginResponse
import data.model.AccountInfo
import util.JwtUtils
import retrofit2.Response
import java.io.File
import org.json.JSONObject

class LoginRepository {

    private val tokenFile = File("access_token.txt")
    private val refreshTokenFile = File("refresh_token.txt") // THÊM
    private val userInfoFile = File("user_info.json")

    suspend fun login(username: String, password: String): LoginResponse {
        println("\n=== [REPO-1] LOGIN START ===")
        val response = RetrofitInstance.loginApi.login(LoginRequest(username, password))

        println("[REPO-2] Response code: ${response.code()}")
        println("[REPO-3] Response successful: ${response.isSuccessful}")

        if (response.isSuccessful) {
            // IN RA TẤT CẢ HEADERS
            println("[REPO-4] ALL HEADERS:")
            response.headers().forEach { (name, value) ->
                println("  $name: $value")
            }

            // Lấy tất cả Set-Cookie
            val allCookies = response.headers().values("Set-Cookie")
            println("[REPO-5] Set-Cookie count: ${allCookies.size}")

            allCookies.forEachIndexed { index, cookie ->
                println("[REPO-6.$index] Full cookie: $cookie")
            }

            var accessTokenSaved = false
            var refreshTokenSaved = false
            var userInfoSaved = false

            allCookies.forEach { cookie ->
                println("[REPO-7] Processing: ${cookie.take(100)}...")

                when {
                    cookie.contains("access_token=") -> {
                        println("[REPO-8] ✅ Found access_token cookie!")
                        val token = cookie.substringAfter("access_token=").substringBefore(";")
                        println("[REPO-9] Extracted token: ${token.take(50)}...")
                        tokenFile.writeText(token)
                        accessTokenSaved = true
                        println("[REPO-10] ✅ Access token saved to: ${tokenFile.absolutePath}")

                        val info = JwtUtils.parseAccountInfo(token)
                        if (info != null) {
                            val json = JSONObject().apply {
                                put("accountId", info.accountId)
                                put("username", info.username)
                                put("role", info.role)
                            }
                            userInfoFile.writeText(json.toString())
                            userInfoSaved = true
                            println("[REPO-11] ✅ User info saved: id=${info.accountId}, role='${info.role}'")
                        } else {
                            println("[REPO-11] ❌ Failed to parse user info")
                        }
                    }
                    cookie.contains("refresh_token=") -> {
                        println("[REPO-12] ✅ Found refresh_token cookie!")
                        val token = cookie.substringAfter("refresh_token=").substringBefore(";")
                        File("refresh_token.txt").writeText(token)
                        refreshTokenSaved = true
                        println("[REPO-13] ✅ Refresh token saved")
                    }
                    else -> {
                        println("[REPO-14] ⚠️ Unknown cookie type")
                    }
                }
            }

            println("[REPO-15] Summary:")
            println("  - Access token saved: $accessTokenSaved")
            println("  - Refresh token saved: $refreshTokenSaved")
            println("  - User info saved: $userInfoSaved")

            // Verify files
            println("[REPO-16] File verification:")
            println("  - access_token.txt exists: ${tokenFile.exists()}")
            println("  - access_token.txt path: ${tokenFile.absolutePath}")
            println("  - refresh_token.txt exists: ${File("refresh_token.txt").exists()}")
            println("  - user_info.json exists: ${userInfoFile.exists()}")

            println("=== [REPO-17] LOGIN END ===\n")
            return response.body() ?: LoginResponse(null, "Lỗi không có dữ liệu")
        } else {
            println("[REPO-18] ❌ Login failed")
            return LoginResponse(null, "Đăng nhập thất bại")
        }
    }

    fun getAccessToken(): String? {
        return if (tokenFile.exists()) tokenFile.readText() else null
    }

    fun getRefreshToken(): String? {
        return if (refreshTokenFile.exists()) refreshTokenFile.readText() else null
    }

    fun getUserInfo(): AccountInfo? {
        if (userInfoFile.exists()) {
            val json = JSONObject(userInfoFile.readText())
            val accountId = json.optInt("accountId", -1)
            val username = json.optString("username", "")
            val role = json.optString("role", "")
            return AccountInfo(accountId, username, role)
        }
        return null
    }

    fun logout() {
        if (tokenFile.exists()) tokenFile.delete()
        if (refreshTokenFile.exists()) refreshTokenFile.delete() // THÊM
        if (userInfoFile.exists()) userInfoFile.delete()
        println("[Logout] All tokens cleared")
    }
}