// File: com/example/dat_lich_kham_fe/websocket/helpers/NotificationWebSocketHelper.kt
package com.example.dat_lich_kham_fe.websocket.helpers

import android.content.Context
import android.util.Log
import com.example.dat_lich_kham_fe.data.model.NotificationListResponse
import com.example.dat_lich_kham_fe.util.PersistentCookieJar
import com.example.dat_lich_kham_fe.websocket.handlers.NotificationWebSocketHandler
import kotlinx.coroutines.runBlocking

object NotificationWebSocketHelper {

    /**
     * Lấy access token từ PersistentCookieJar (singleton)
     * KHÔNG tạo DataStore mới - dùng instance hiện có
     */
    private fun getAccessToken(context: Context): String? {
        return try {
            val cookieJar = PersistentCookieJar(context)

            val token = runBlocking {
                // Check xem có token không
                val hasToken = cookieJar.hasToken()
                Log.d("WebSocketHelper", "hasToken: $hasToken")

                if (hasToken) {
                    // Lấy account info để verify
                    val accountId = cookieJar.getAccountId()
                    Log.d("WebSocketHelper", "User accountId: $accountId")

                    // Lấy raw cookie value từ loadForRequest
                    // PersistentCookieJar sẽ load token từ DataStore singleton
                    val cookie = cookieJar.loadForRequest(okhttp3.HttpUrl.Builder()
                        .scheme("https")
                        .host("example.com")
                        .build()).firstOrNull()

                    val tokenValue = cookie?.value
                    Log.d("WebSocketHelper", "Token: ${if (tokenValue != null) "FOUND (${tokenValue.take(20)}...)" else "NOT FOUND"}")
                    tokenValue
                } else {
                    Log.w("WebSocketHelper", "User not logged in - no token")
                    null
                }
            }

            token
        } catch (e: Exception) {
            Log.e("WebSocketHelper", "Error getting token: ${e.message}", e)
            null
        }
    }

    /**
     * Tạo WebSocket handler cho notification
     */
    fun createHandler(
        context: Context,
        baseUrl: String,
        onNotificationReceived: (NotificationListResponse) -> Unit,
        onConnected: (() -> Unit)? = null,
        onDisconnected: (() -> Unit)? = null,
        onError: ((String) -> Unit)? = null
    ): NotificationWebSocketHandler {
        // FIX: Lấy token từ PersistentCookieJar (không tạo DataStore mới)
        val token = getAccessToken(context)

        if (token.isNullOrBlank()) {
            Log.e("WebSocketHelper", "ERROR: No token found! User needs to login first")
            onError?.invoke("No token found - please login")
        } else {
            Log.d("WebSocketHelper", "✓ Token found, WebSocket handler created")
        }

        return NotificationWebSocketHandler(
            baseUrl = baseUrl,
            token = token ?: "",
            onNotificationReceived = onNotificationReceived,
            onConnected = onConnected,
            onDisconnected = onDisconnected,
            onError = onError
        )
    }
}