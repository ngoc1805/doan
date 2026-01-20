// File: com/example/dat_lich_kham_fe/websocket/handlers/NotificationWebSocketHandler.kt
package com.example.dat_lich_kham_fe.websocket.handlers

import android.util.Log
import com.example.dat_lich_kham_fe.data.model.NotificationListResponse
import com.example.dat_lich_kham_fe.websocket.WebSocketManager
import kotlinx.serialization.json.Json

/**
 * Handler cho WebSocket notification ở Frontend
 * Xử lý logic riêng cho notification
 */
class NotificationWebSocketHandler(
    private val baseUrl: String,
    private val token: String,  // ← THÊM token
    private val onNotificationReceived: (NotificationListResponse) -> Unit,
    private val onConnected: (() -> Unit)? = null,
    private val onDisconnected: (() -> Unit)? = null,
    private val onError: ((String) -> Unit)? = null
) {
    private var webSocketManager: WebSocketManager? = null
    private val json = Json { ignoreUnknownKeys = true }

    /**
     * Kết nối WebSocket cho notification
     */
    fun connect(userId: Int, page: Int = 1, pageSize: Int = 30) {
        // FIX: Escape token string để tránh JSON error
        val escapedToken = token.replace("\"", "\\\"")
        val initialMessage = """{"type":"connect","userId":$userId,"token":"$escapedToken","page":$page,"pageSize":$pageSize}"""

        Log.d("NotificationWS", "Connecting with token: ${token.take(20)}...")
        Log.d("NotificationWS", "Message: $initialMessage")

        webSocketManager = WebSocketManager(
            baseUrl = baseUrl,
            endpoint = "/ws/notifications",
            onMessage = { message ->
                try {
                    // Kiểm tra xem có phải error không
                    if (message.contains("\"type\":\"error\"")) {
                        Log.e("NotificationWS", "Server error: $message")
                        onError?.invoke(message)
                        return@WebSocketManager
                    }

                    val response = json.decodeFromString<NotificationListResponse>(message)
                    Log.d("NotificationWS", "Received ${response.notifications.size} notifications")
                    onNotificationReceived(response)
                } catch (e: Exception) {
                    Log.e("NotificationWS", "Failed to parse notification: ${e.message}")
                    onError?.invoke("Failed to parse notification: ${e.message}")
                }
            },
            onConnected = {
                Log.d("NotificationWS", "Connected successfully")
                onConnected?.invoke()
            },
            onDisconnected = {
                Log.d("NotificationWS", "Disconnected")
                onDisconnected?.invoke()
            },
            onError = { error ->
                Log.e("NotificationWS", "Error: $error")
                onError?.invoke(error)
            }
        )

        webSocketManager?.connect("notification_$userId", initialMessage)
    }

    /**
     * Load thêm notifications
     */
    fun loadMore(userId: Int, page: Int, pageSize: Int) {
        val message = """{"type":"load_more","userId":$userId,"page":$page,"pageSize":$pageSize}"""
        Log.d("NotificationWS", "Sending load_more: page=$page")
        webSocketManager?.send(message)
    }

    /**
     * Ngắt kết nối
     */
    fun disconnect() {
        Log.d("NotificationWS", "Disconnecting")
        webSocketManager?.disconnect()
        webSocketManager = null
    }

    /**
     * Kiểm tra trạng thái kết nối
     */
    fun isConnected(): Boolean {
        return webSocketManager?.isConnected() == true
    }
}