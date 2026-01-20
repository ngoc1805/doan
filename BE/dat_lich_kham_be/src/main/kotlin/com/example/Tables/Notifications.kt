package com.example.Tables

import com.example.Tables.Roles.default
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant

object Notifications : IntIdTable("notifications") {
    val userId = reference("user_id", Users, onDelete = ReferenceOption.CASCADE) // Khóa ngoại tới bảng Users
    val content = varchar("content", 512)
    val isSeen = bool("is_seen").default(false)      // Đã xem (mặc định false)
    val isReceived = bool("is_received").default(false) // Đã nhận (mặc định false)
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp())
    val path = varchar("path", 255)
}