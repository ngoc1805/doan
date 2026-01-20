package com.example.dat_lich_kham_fe.util

import android.content.Context
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

class PersistentCookieJar(context: Context) : CookieJar {

    private val tokenStorage = SecureTokenStorage(context)

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        cookies.forEach { cookie ->
            when (cookie.name) {
                "access_token" -> {
                    tokenStorage.saveAccessToken(cookie.value)
                }
                "refresh_token" -> {
                    tokenStorage.saveRefreshToken(cookie.value)
                }
            }
        }
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        return try {
            val cookies = mutableListOf<Cookie>()

            // Thêm Access Token vào request
            tokenStorage.getAccessToken()?.let { accessToken ->
                cookies.add(
                    Cookie.Builder()
                        .name("access_token")
                        .value(accessToken)
                        .domain(url.host)
                        .path("/")
                        .build()
                )
            }

            // Thêm Refresh Token cho endpoint refresh
            if (url.encodedPath.contains("/api/auth/refresh")) {
                tokenStorage.getRefreshToken()?.let { refreshToken ->
                    // Kiểm tra refresh token chưa hết hạn
                    if (!tokenStorage.isTokenExpired(refreshToken)) {
                        cookies.add(
                            Cookie.Builder()
                                .name("refresh_token")
                                .value(refreshToken)
                                .domain(url.host)
                                .path("/")
                                .build()
                        )
                    }
                }
            }

            cookies
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    // Các hàm truy cập token - giờ đồng bộ thay vì suspend
    fun getRefreshToken(): String? = tokenStorage.getRefreshToken()

    fun getAccessToken(): String? = tokenStorage.getAccessToken()

    fun getAccountId(): String? = tokenStorage.getUserId()

    fun getUsername(): String? = tokenStorage.getUsername()

    fun isLoggedIn(): Boolean = tokenStorage.isLoggedIn()

    fun hasToken(): Boolean = tokenStorage.hasToken()

    fun saveInfoUpdated(isUpdated: Boolean) = tokenStorage.saveInfoUpdated(isUpdated)

    fun isInfoUpdated(): Boolean = tokenStorage.isInfoUpdated()

    fun logout() = tokenStorage.logout()

    fun isAccessTokenExpired(): Boolean = tokenStorage.isAccessTokenExpired()
}