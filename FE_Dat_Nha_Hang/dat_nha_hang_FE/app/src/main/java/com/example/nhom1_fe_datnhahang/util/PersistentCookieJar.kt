package com.example.nhom1_fe_datnhahang.util

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import androidx.datastore.preferences.preferencesDataStore

private val Context.dataStore by preferencesDataStore(name = "auth_prefs")

class PersistentCookieJar(private val context: Context) : CookieJar {
    companion object {
        private val COOKIE_ACCESS = stringPreferencesKey("access_token")
        private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private val USER_ID = stringPreferencesKey("user_id")
        private val USERNAME = stringPreferencesKey("username")
        private val INFO_UPDATED = booleanPreferencesKey("is_info_updated")
        private val ROLE = stringPreferencesKey("role")
    }

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        cookies.forEach { cookie ->
            if (cookie.name == "access_token") {
                kotlinx.coroutines.runBlocking {
                    // Decode JWT để lấy id, username, role
                    val payload = decodeJwtPayload(cookie.value)
                    val userId = payload?.optInt("id")?.toString() ?: ""
                    val username = payload?.optString("username") ?: ""
                    val role = payload?.optString("role") ?: ""

                    context.dataStore.edit { preferences ->
                        preferences[COOKIE_ACCESS] = cookie.value
                        preferences[IS_LOGGED_IN] = true
                        preferences[USER_ID] = userId
                        preferences[USERNAME] = username
                        preferences[ROLE] = role
                    }
                }
            }
        }
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        return try {
            kotlinx.coroutines.runBlocking {
                val accessToken = context.dataStore.data.first()[COOKIE_ACCESS]
                if (accessToken != null) {
                    listOf(
                        Cookie.Builder()
                            .name("access_token")
                            .value(accessToken)
                            .domain(url.host)
                            .path("/")
                            .httpOnly()
                            .build()
                    )
                } else {
                    emptyList()
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Lấy ra giá trị từ DataStore
    suspend fun getRole(): String? {
        return context.dataStore.data.first()[ROLE]
    }

    suspend fun getaccountId(): String? {
        return context.dataStore.data.first()[USER_ID]
    }

    suspend fun getUsername(): String? {
        return context.dataStore.data.first()[USERNAME]
    }

    suspend fun isLoggedIn(): Boolean {
        return context.dataStore.data.first()[IS_LOGGED_IN] ?: false
    }

    suspend fun logout() {
        context.dataStore.edit { preferences ->
            preferences.remove(COOKIE_ACCESS)
            preferences.remove(USER_ID)
            preferences.remove(USERNAME)
            preferences.remove(INFO_UPDATED)
            preferences.remove(ROLE)
            preferences[IS_LOGGED_IN] = false
        }
    }

    suspend fun hasToken(): Boolean {
        val token = context.dataStore.data.first()[COOKIE_ACCESS]
        return !token.isNullOrEmpty()
    }

    suspend fun saveInfoUpdated(isUpdated: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[INFO_UPDATED] = isUpdated
        }
    }

    suspend fun isInfoUpdated(): Boolean {
        return context.dataStore.data.first()[INFO_UPDATED] ?: false
    }
}
