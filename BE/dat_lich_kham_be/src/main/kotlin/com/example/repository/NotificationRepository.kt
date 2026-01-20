package com.example.repository

import com.example.Tables.Notifications
import com.example.Tables.Notifications.isReceived
import com.example.Tables.Users
import com.example.dao.NotificationDAO
import com.example.dto.Request.NotificationRequest
import com.example.models.Notification
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class NotificationRepository {
    fun createNotification(request: NotificationRequest): Notification {
        return transaction {
            val dao = NotificationDAO.new {
                userId = EntityID(request.userId, Users)
                content = request.content
                path = request.path
            }
            dao.toModel()
        }
    }
    //
    fun getNotificationsByUserIdPaged(userId: Int, page: Int, pageSize: Int): List<Notification> {
        return transaction {
            NotificationDAO
                .find { Notifications.userId eq userId }
                .orderBy(Notifications.createdAt to SortOrder.DESC)
                .limit(pageSize, offset = ((page - 1) * pageSize).toLong())
                .map { it.toModel() }
        }
    }
    //
    fun areAllNotificationsReceived(userId: Int): Boolean {
        return transaction {
            // Trả về true nếu không có thông báo nào chưa nhận
            NotificationDAO.find { (Notifications.userId eq userId) and (Notifications.isReceived eq false) }
                .empty()
        }
    }
    //
    fun setAllNotificationsReceived(userId: Int) {
        transaction {
            NotificationDAO.find { Notifications.userId eq userId }.forEach { dao ->
                dao.isReceived = true
            }
        }
    }
    //
    fun markNotificationSeen(notificationId: Int): Boolean {
        return transaction {
            val dao = NotificationDAO.findById(notificationId)
            if (dao != null) {
                dao.isSeen = true
                true
            } else {
                false
            }
        }
    }
}