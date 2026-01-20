package com.example.service

import com.example.dto.Request.NotificationRequest
import com.example.dto.Response.NotificationItem
import com.example.models.Notification
import com.example.repository.NotificationRepository
import com.example.websocket.helpers.NotificationWebSocketHelper
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("NotificationService")

class NotificationService(
    private val repository: NotificationRepository = NotificationRepository()
) {
    suspend fun createNotification(request: NotificationRequest): Notification {
        logger.info("Creating notification for userId: ${request.userId}, content: ${request.content}")

        val notification = repository.createNotification(request)
        logger.info("Notification created with id: ${notification.id}")

        val notificationItem = NotificationItem(
            id = notification.id,
            content = notification.content,
            isSeen = notification.isSeen,
            createdAt = notification.createdAt,
            path = notification.path
        )

        logger.info("Sending to WebSocket - userId: ${request.userId}, notificationId: ${notification.id}")

        try {
            // Kiểm tra user có online không
            val isOnline = NotificationWebSocketHelper.isUserOnline(request.userId)
            val connectionCount = NotificationWebSocketHelper.getUserConnectionCount(request.userId)

            logger.info("User ${request.userId} isOnline: $isOnline, connections: $connectionCount")

            if (isOnline) {
                NotificationWebSocketHelper.sendToUser(request.userId, notificationItem)
                logger.info("Notification sent successfully via WebSocket")
            } else {
                logger.warn("User ${request.userId} is not online, notification only saved to DB")
            }
        } catch (e: Exception) {
            logger.error("Error sending notification via WebSocket", e)
        }

        return notification
    }

    fun areAllNotificationsReceived(userId: Int): Boolean {
        return repository.areAllNotificationsReceived(userId)
    }

    fun setAllNotificationsReceived(userId: Int) {
        repository.setAllNotificationsReceived(userId)
    }

    fun getNotificationsByUserIdPaged(userId: Int, page: Int, pageSize: Int): List<Notification> {
        return repository.getNotificationsByUserIdPaged(userId, page, pageSize)
    }

    fun markNotificationSeen(notificationId: Int): Boolean {
        return repository.markNotificationSeen(notificationId)
    }
}