package com.example.dao

import com.example.Tables.Notifications
import com.example.Tables.Users
import com.example.utils.toKotlinxLocalDate
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import com.example.utils.toKotlinxLocalDateTime

class NotificationDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<NotificationDAO>(Notifications)

    var userId by Notifications.userId
    var content by Notifications.content
    var isSeen by Notifications.isSeen
    var isReceived by Notifications.isReceived
    val createdAt by Notifications.createdAt
    var path by Notifications.path

    fun toModel(): com.example.models.Notification {
        return com.example.models.Notification(
            id = id.value,
            userId = userId.value,
            content = content,
            isSeen = isSeen,
            isReceived = isReceived,
            createdAt = createdAt.toKotlinxLocalDateTime(),
            path = path
        )
    }
}