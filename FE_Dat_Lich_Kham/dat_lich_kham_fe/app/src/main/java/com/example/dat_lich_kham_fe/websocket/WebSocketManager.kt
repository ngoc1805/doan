// File: com/example/dat_lich_kham_fe/websocket/WebSocketManager.kt
package com.example.dat_lich_kham_fe.websocket

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.*
import java.util.concurrent.TimeUnit

/**
 * Quản lý tất cả WebSocket connections của app
 * Core class cho tất cả các loại WebSocket
 */
class WebSocketManager(
    private val baseUrl: String,
    private val endpoint: String,
    private val onMessage: (String) -> Unit,
    private val onConnected: (() -> Unit)? = null,
    private val onDisconnected: (() -> Unit)? = null,
    private val onError: ((String) -> Unit)? = null
) {
    private var webSocket: WebSocket? = null
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)   // FIX: Tăng từ 10 lên 30
        .readTimeout(30, TimeUnit.SECONDS)      // FIX: Tăng từ 10 lên 30
        .writeTimeout(30, TimeUnit.SECONDS)     // FIX: Tăng từ 10 lên 30
        .pingInterval(15, TimeUnit.SECONDS)     // Gửi ping mỗi 15s
        .build()

    private var reconnectJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO)
    private var isConnected = false
    private var shouldReconnect = true
    private var connectionKey: String? = null

    fun connect(key: String, initialMessage: String? = null) {
        connectionKey = key
        shouldReconnect = true

        val wsUrl = baseUrl.replace("http://", "ws://").replace("https://", "wss://")
        val request = Request.Builder()
            .url("$wsUrl$endpoint")
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d("WebSocket", "[$key] Connected")
                isConnected = true
                onConnected?.invoke()

                // Gửi message khởi tạo nếu có
                initialMessage?.let { webSocket.send(it) }

                // Bắt đầu ping
                startPing()
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d("WebSocket", "[$key] Received: $text")
                try {
                    if (text.contains("\"type\":\"pong\"")) {
                        Log.d("WebSocket", "[$key] Pong received")
                        return
                    }
                    onMessage(text)
                } catch (e: Exception) {
                    Log.e("WebSocket", "[$key] Parse error: ${e.message}")
                    onError?.invoke(e.message ?: "Unknown error")
                }
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                Log.d("WebSocket", "[$key] Closing: $code - $reason")
                isConnected = false
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d("WebSocket", "[$key] Closed: $code - $reason")
                isConnected = false
                onDisconnected?.invoke()
                if (shouldReconnect) {
                    reconnect(key, initialMessage)
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e("WebSocket", "[$key] Error: ${t.message}")
                isConnected = false
                onError?.invoke(t.message ?: "Connection failed")
                if (shouldReconnect) {
                    reconnect(key, initialMessage)
                }
            }
        })
    }

    private fun startPing() {
        reconnectJob?.cancel()
        reconnectJob = scope.launch {
            while (isConnected && shouldReconnect) {
                delay(15000) // Ping mỗi 15 giây
                try {
                    if (isConnected) {
                        send("""{"type":"ping"}""")
                    }
                } catch (e: Exception) {
                    Log.e("WebSocket", "[${connectionKey}] Ping error: ${e.message}")
                }
            }
        }
    }

    private fun reconnect(key: String, initialMessage: String?) {
        reconnectJob?.cancel()
        reconnectJob = scope.launch {
            Log.d("WebSocket", "[$key] Attempting to reconnect in 3 seconds...")
            delay(3000) // Đợi 3 giây trước khi reconnect
            if (shouldReconnect) {
                connect(key, initialMessage)
            }
        }
    }

    fun send(message: String) {
        if (isConnected) {
            webSocket?.send(message)
            Log.d("WebSocket", "[${connectionKey}] Sent: $message")
        } else {
            Log.w("WebSocket", "[${connectionKey}] Cannot send, not connected")
        }
    }

    fun disconnect() {
        shouldReconnect = false
        reconnectJob?.cancel()
        webSocket?.close(1000, "User disconnect")
        webSocket = null
        isConnected = false
        Log.d("WebSocket", "[${connectionKey}] Disconnected")
    }

    fun isConnected(): Boolean = isConnected
}