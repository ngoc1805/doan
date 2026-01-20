// File: com/example/websocket/WebSocketManager.kt
package com.example.websocket

import io.ktor.websocket.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Quản lý tất cả WebSocket connections của hệ thống
 * Có thể mở rộng cho nhiều loại WebSocket khác nhau
 */
object WebSocketManager {
    // Key: "notification_{userId}" hoặc "chat_{roomId}" hoặc "order_{orderId}"
    private val connections = ConcurrentHashMap<String, MutableSet<WebSocketSession>>()

    /**
     * Thêm connection
     * @param key: Ví dụ: "notification_123", "chat_room_456"
     */
    fun addConnection(key: String, session: WebSocketSession) {
        connections.getOrPut(key) { mutableSetOf() }.add(session)
        println("[$key] Connected. Total: ${connections[key]?.size}")
    }

    /**
     * Xóa connection
     */
    fun removeConnection(key: String, session: WebSocketSession) {
        connections[key]?.remove(session)
        if (connections[key]?.isEmpty() == true) {
            connections.remove(key)
        }
        println("[$key] Disconnected. Remaining: ${connections[key]?.size ?: 0}")
    }

    /**
     * Gửi message đến tất cả connections của một key
     */
    suspend fun broadcast(key: String, message: String) {
        val sessions = connections[key]
        if (sessions.isNullOrEmpty()) {
            println("[$key] No active connections")
            return
        }

        sessions.forEach { session ->
            try {
                session.send(Frame.Text(message))
                println("[$key] Message sent")
            } catch (e: Exception) {
                println("[$key] Failed to send: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    /**
     * Gửi message đến nhiều keys
     * Ví dụ: Gửi cho tất cả user trong một room
     */
    suspend fun broadcastToMultiple(keys: List<String>, message: String) {
        keys.forEach { key ->
            broadcast(key, message)
        }
    }

    /**
     * Lấy số lượng connections của một key
     */
    fun getConnectionCount(key: String): Int {
        return connections[key]?.size ?: 0
    }

    /**
     * Kiểm tra key có đang online không
     */
    fun isOnline(key: String): Boolean {
        return connections[key]?.isNotEmpty() == true
    }
}