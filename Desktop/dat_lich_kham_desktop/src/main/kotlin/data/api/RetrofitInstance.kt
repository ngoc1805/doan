//package data.api
//
//import data.model.ServiceAppointmentRequest
//import okhttp3.OkHttpClient
//import okhttp3.Interceptor
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//import java.io.File
//
//var address = "http://localhost:8080/"
//
//val tokenInterceptor = Interceptor { chain ->
//    val tokenFile = File("access_token.txt")
//    val token = if (tokenFile.exists()) tokenFile.readText().trim() else null
//    val request = if (token != null && token.isNotBlank()) {
//        chain.request().newBuilder()
//            .addHeader("Cookie", "access_token=$token")
//            .build()
//    } else {
//        chain.request()
//    }
//    chain.proceed(request)
//}
//
//object RetrofitInstance {
//    private val client = OkHttpClient.Builder()
//        .addInterceptor(tokenInterceptor)
//        .build()
//
//    val retrofit: Retrofit = Retrofit.Builder()
//        .baseUrl(address)
//        .client(client)
//        .addConverterFactory(GsonConverterFactory.create())
//        .build()
//
//    val loginApi: LoginApi = retrofit.create(LoginApi::class.java)
//
//    val doctorApi: DoctorApi = retrofit.create(DoctorApi::class.java)
//
//    val appointmentApi: AppointmentApi = retrofit.create(AppointmentApi::class.java)
//
//    val serviceRoomApi: ServiceRoomApi = retrofit.create(ServiceRoomApi::class.java)
//
//    val serviceAppointmentApi: ServiceAppointmentApi = retrofit.create(ServiceAppointmentApi::class.java)
//
//    val notificationApi: NotificationApi = retrofit.create(NotificationApi::class.java)
//
//    val fileApi: FileApi = retrofit.create(FileApi::class.java)
//
//    val resultApi: ResultApi = retrofit.create(ResultApi::class.java)
//
//    val departmentApi: DepartmentApi = retrofit.create(DepartmentApi::class.java)
//
//    val inpatientApi: InpatientApi = retrofit.create(InpatientApi::class.java)
//
//    val accountApi : AccountApi = retrofit.create(AccountApi::class.java)
//}

package data.api

import okhttp3.OkHttpClient
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

var address = "http://localhost:8080/"

// Interceptor cũ - đính kèm token vào mọi request
val tokenInterceptor = Interceptor { chain ->
    val tokenFile = File("access_token.txt")
    val refreshTokenFile = File("refresh_token.txt")

    val originalRequest = chain.request()
    val path = originalRequest.url.encodedPath

    // Đọc token từ file
    val accessToken = if (tokenFile.exists()) tokenFile.readText().trim() else null
    val refreshToken = if (refreshTokenFile.exists()) refreshTokenFile.readText().trim() else null

    // Tạo cookie header
    val cookieHeader = buildString {
        if (accessToken != null && accessToken.isNotBlank()) {
            append("access_token=$accessToken")
        }
        // CHỈ thêm refresh_token nếu đang gọi /api/auth/refresh
        if (path.contains("/api/auth/refresh") && refreshToken != null && refreshToken.isNotBlank()) {
            if (isNotEmpty()) append("; ")
            append("refresh_token=$refreshToken")
        }
    }

    val request = if (cookieHeader.isNotBlank()) {
        originalRequest.newBuilder()
            .addHeader("Cookie", cookieHeader)
            .build()
    } else {
        originalRequest
    }

    chain.proceed(request)
}

// THÊM INTERCEPTOR MỚI - Tự động refresh token
val authInterceptor = Interceptor { chain ->
    val originalRequest = chain.request()
    val path = originalRequest.url.encodedPath

    // Bỏ qua các endpoint không cần auth
    val skipPaths = listOf(
        "/api/auth/login",
        "/api/auth/register",
        "/api/auth/refresh",
        "/api/auth/send-otp",
        "/api/auth/verify-otp"
    )

    if (skipPaths.any { path.contains(it) }) {
        return@Interceptor chain.proceed(originalRequest)
    }

    // Thực hiện request ban đầu
    val response = chain.proceed(originalRequest)

    // Nếu 401 Unauthorized -> thử refresh token
    if (response.code == 401) {
        response.close()
        println("[AuthInterceptor] Got 401, attempting refresh...")

        val tokenFile = File("access_token.txt")
        val refreshTokenFile = File("refresh_token.txt")
        val userInfoFile = File("user_info.json")

        // Kiểm tra có refresh token không
        val hasRefreshToken = refreshTokenFile.exists() && refreshTokenFile.readText().trim().isNotBlank()

        if (!hasRefreshToken) {
            println("[AuthInterceptor] No refresh token, logout required")
            tokenFile.delete()
            refreshTokenFile.delete()
            userInfoFile.delete()
            return@Interceptor response
        }

        // Gọi API refresh
        try {
            val refreshRequest = okhttp3.Request.Builder()
                .url("${address}api/auth/refresh")
                .post("".toRequestBody("application/json".toMediaType()))
                .build()

            val refreshResponse = chain.proceed(refreshRequest)

            if (refreshResponse.isSuccessful) {
                // Lưu token mới từ Set-Cookie header
                val setCookie = refreshResponse.headers["Set-Cookie"]
                if (setCookie != null && setCookie.contains("access_token=")) {
                    val newAccessToken = setCookie.substringAfter("access_token=").substringBefore(";")
                    tokenFile.writeText(newAccessToken)
                    println("[AuthInterceptor] New access token saved")
                }

                // Có thể có refresh_token mới (nếu backend dùng rotating refresh token)
                if (setCookie != null && setCookie.contains("refresh_token=")) {
                    val newRefreshToken = setCookie.substringAfter("refresh_token=").substringBefore(";")
                    refreshTokenFile.writeText(newRefreshToken)
                    println("[AuthInterceptor] New refresh token saved")
                }

                refreshResponse.close()

                println("[AuthInterceptor] Refresh successful, retrying original request")
                // Retry request ban đầu
                val newRequest = originalRequest.newBuilder().build()
                return@Interceptor chain.proceed(newRequest)
            } else {
                println("[AuthInterceptor] Refresh failed with code ${refreshResponse.code}, logout required")
                refreshResponse.close()

                // Logout
                tokenFile.delete()
                refreshTokenFile.delete()
                userInfoFile.delete()
            }
        } catch (e: Exception) {
            println("[AuthInterceptor] Refresh error: ${e.message}")
            e.printStackTrace()

            // Logout on error
            tokenFile.delete()
            refreshTokenFile.delete()
            userInfoFile.delete()
        }
    }

    response
}

object RetrofitInstance {
    private val client = OkHttpClient.Builder()
        .addInterceptor(tokenInterceptor)    // Thêm token vào request
        .addInterceptor(authInterceptor)     // Tự động refresh khi 401
        .build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(address)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val loginApi: LoginApi = retrofit.create(LoginApi::class.java)
    val doctorApi: DoctorApi = retrofit.create(DoctorApi::class.java)
    val appointmentApi: AppointmentApi = retrofit.create(AppointmentApi::class.java)
    val serviceRoomApi: ServiceRoomApi = retrofit.create(ServiceRoomApi::class.java)
    val serviceAppointmentApi: ServiceAppointmentApi = retrofit.create(ServiceAppointmentApi::class.java)
    val notificationApi: NotificationApi = retrofit.create(NotificationApi::class.java)
    val fileApi: FileApi = retrofit.create(FileApi::class.java)
    val resultApi: ResultApi = retrofit.create(ResultApi::class.java)
    val departmentApi: DepartmentApi = retrofit.create(DepartmentApi::class.java)
    val inpatientApi: InpatientApi = retrofit.create(InpatientApi::class.java)
    val accountApi: AccountApi = retrofit.create(AccountApi::class.java)
}