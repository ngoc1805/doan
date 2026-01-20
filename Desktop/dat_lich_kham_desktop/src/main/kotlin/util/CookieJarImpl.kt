//package util
//
//import okhttp3.Cookie
//import okhttp3.CookieJar
//import okhttp3.HttpUrl
//
//class CookieJarImpl : CookieJar {
//    private val cookieStore: MutableMap<String, List<Cookie>> = mutableMapOf()
//
//    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
//        cookieStore[url.host] = cookies
//    }
//
//    override fun loadForRequest(url: HttpUrl): List<Cookie> {
//        return cookieStore[url.host] ?: listOf()
//    }
//
//    // Helper để lấy cookie access_token
//    fun getAccessToken(host: String): String? {
//        val cookies = cookieStore[host]
//        return cookies?.find { it.name == "access_token" }?.value
//    }
//}

package util

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

class CookieJarImpl : CookieJar {
    private val cookieStore: MutableMap<String, List<Cookie>> = mutableMapOf()

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        cookieStore[url.host] = cookies
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val host = url.host
        val path = url.encodedPath

        val cookies = mutableListOf<Cookie>()

        // Luôn thêm access_token
        cookieStore[host]?.find { it.name == "access_token" }?.let {
            cookies.add(it)
        }

        // CHỈ thêm refresh_token nếu đang gọi endpoint /api/auth/refresh
        if (path.contains("/api/auth/refresh")) {
            cookieStore[host]?.find { it.name == "refresh_token" }?.let {
                cookies.add(it)
            }
        }

        return cookies
    }

    // Lấy access token
    fun getAccessToken(host: String): String? {
        return cookieStore[host]?.find { it.name == "access_token" }?.value
    }

    // Lấy refresh token
    fun getRefreshToken(host: String): String? {
        return cookieStore[host]?.find { it.name == "refresh_token" }?.value
    }

    // Xóa tất cả cookies (khi logout)
    fun clearAll() {
        cookieStore.clear()
    }
}