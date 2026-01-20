// File: com/example/websocket/helpers/NotificationWebSocketHelper.kt
package com.example.websocket.helpers

import com.example.dto.Response.NotificationItem
import com.example.dto.Response.NotificationListResponse
import com.example.websocket.WebSocketManager
import kotlinx.serialization.json.Json

/**
 * Helper functions cho Notification WebSocket
 * Tách logic gửi notification ra khỏi Service
 */
object NotificationWebSocketHelper {

    /**
     * Gửi notification realtime cho 1 user
     * FIX: Gửi đúng format NotificationListResponse (danh sách)
     */
    suspend fun sendToUser(userId: Int, notificationItem: NotificationItem) {
        // Wrap notification vào danh sách
        val response = NotificationListResponse(listOf(notificationItem))
        val json = Json.encodeToString(NotificationListResponse.serializer(), response)
        val key = "notification_$userId"

        WebSocketManager.broadcast(key, json)
    }

    /**
     * Gửi notification cho nhiều users
     */
    suspend fun sendToMultipleUsers(userIds: List<Int>, notificationItem: NotificationItem) {
        val response = NotificationListResponse(listOf(notificationItem))
        val json = Json.encodeToString(NotificationListResponse.serializer(), response)
        val keys = userIds.map { "notification_$it" }

        WebSocketManager.broadcastToMultiple(keys, json)
    }

    /**
     * Kiểm tra user có đang online không
     */
    fun isUserOnline(userId: Int): Boolean {
        return WebSocketManager.isOnline("notification_$userId")
    }

    /**
     * Đếm số connections của user
     */
    fun getUserConnectionCount(userId: Int): Int {
        return WebSocketManager.getConnectionCount("notification_$userId")
    }
}