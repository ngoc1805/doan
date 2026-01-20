package com.example.dat_lich_kham_fe.util

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SecureTokenStorage(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secure_auth_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USERNAME = "username"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_INFO_UPDATED = "is_info_updated"
    }

    // Lưu Access Token
    fun saveAccessToken(token: String) {
        sharedPreferences.edit().putString(KEY_ACCESS_TOKEN, token).apply()

        // Decode và lưu thông tin user
        val payload = decodeJwtPayload(token)
        payload?.let {
            val userId = it.optInt("id").toString()
            val username = it.optString("username", "")
            saveUserId(userId)
            saveUsername(username)
            setLoggedIn(true)
        }
    }

    // Lưu Refresh Token
    fun saveRefreshToken(token: String) {
        sharedPreferences.edit().putString(KEY_REFRESH_TOKEN, token).apply()
    }

    // Lấy Access Token
    fun getAccessToken(): String? {
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null)
    }

    // Lấy Refresh Token
    fun getRefreshToken(): String? {
        return sharedPreferences.getString(KEY_REFRESH_TOKEN, null)
    }

    // Kiểm tra token có hết hạn không
    fun isTokenExpired(token: String?): Boolean {
        if (token == null) return true
        return try {
            val payload = decodeJwtPayload(token) ?: return true
            val exp = payload.optLong("exp", 0) * 1000
            val now = System.currentTimeMillis()
            exp < now
        } catch (e: Exception) {
            true
        }
    }

    // Kiểm tra access token có hết hạn không
    fun isAccessTokenExpired(): Boolean {
        return isTokenExpired(getAccessToken())
    }

    // User ID
    private fun saveUserId(userId: String) {
        sharedPreferences.edit().putString(KEY_USER_ID, userId).apply()
    }

    fun getUserId(): String? {
        return sharedPreferences.getString(KEY_USER_ID, null)
    }

    // Username
    private fun saveUsername(username: String) {
        sharedPreferences.edit().putString(KEY_USERNAME, username).apply()
    }

    fun getUsername(): String? {
        return sharedPreferences.getString(KEY_USERNAME, null)
    }

    // Login status
    private fun setLoggedIn(isLoggedIn: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply()
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    // Info updated status
    fun saveInfoUpdated(isUpdated: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_INFO_UPDATED, isUpdated).apply()
    }

    fun isInfoUpdated(): Boolean {
        return sharedPreferences.getBoolean(KEY_INFO_UPDATED, false)
    }

    // Kiểm tra có token không
    fun hasToken(): Boolean {
        return !getAccessToken().isNullOrEmpty()
    }

    // Logout - xóa tất cả dữ liệu
    fun logout() {
        sharedPreferences.edit().apply {
            remove(KEY_ACCESS_TOKEN)
            remove(KEY_REFRESH_TOKEN)
            remove(KEY_USER_ID)
            remove(KEY_USERNAME)
            remove(KEY_INFO_UPDATED)
            putBoolean(KEY_IS_LOGGED_IN, false)
            apply()
        }
    }

    // Clear all data
    fun clearAll() {
        sharedPreferences.edit().clear().apply()
    }
}