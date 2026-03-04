package com.example.Tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.datetime

// Session chat - mỗi user chỉ có 1 session duy nhất
object ChatSessions : IntIdTable("chat_sessions") {
    val userId = reference("user_id", Users, onDelete = ReferenceOption.CASCADE)
    val title = varchar("title", 500).default("Chat với Bot")
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")
}

// Tin nhắn - lưu toàn bộ chat
object ChatMessages : IntIdTable("chat_messages") {
    val sessionId = reference("session_id", ChatSessions, onDelete = ReferenceOption.CASCADE)
    val role = varchar("role", 50) // "user" hoặc "model"
    val message = text("message")
    val createdAt = datetime("created_at")
    val metadata = text("metadata").nullable() // JSON cho thông tin bổ sung
}

// Bộ nhớ tạm - trong ngày
object TempMemories : IntIdTable("temp_memories") {
    val sessionId = reference("session_id", ChatSessions, onDelete = ReferenceOption.CASCADE)
    val content = text("content") // Thông tin quan trọng tạm thời
    val messageIds = text("message_ids") // JSON array của message IDs liên quan
    val createdAt = datetime("created_at")
    val importance = integer("importance").default(5) // 1-10
}

// Bộ nhớ lâu dài - đã tóm tắt và embedding
object ChatMemories : IntIdTable("chat_memories") {
    val userId = reference("user_id", Users, onDelete = ReferenceOption.CASCADE)
    val sessionId = reference("session_id", ChatSessions, onDelete = ReferenceOption.CASCADE).nullable()
    val summary = text("summary") // Bản tóm tắt ngắn gọn
    val embedding = text("embedding") // Vector embedding (JSON array)
    val category = varchar("category", 100).nullable() // "health", "appointment", "personal_info"
    val importance = integer("importance").default(5)
    val createdAt = datetime("created_at")
    val expiresAt = datetime("expires_at").nullable() // Thời gian hết hạn (nếu có)
    val sourceMessageCount = integer("source_message_count").default(0)
}

// Index để tìm kiếm nhanh
object ChatMemoriesIndex : IntIdTable("chat_memories_index") {
    val memoryId = reference("memory_id", ChatMemories, onDelete = ReferenceOption.CASCADE)
    val keywords = text("keywords") // Từ khóa để search nhanh
    val vectorHash = varchar("vector_hash", 100) // Hash để clustering
}