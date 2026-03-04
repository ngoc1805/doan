package com.example.dao

import com.example.Tables.*
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import kotlinx.datetime.toKotlinLocalDateTime
import kotlinx.datetime.toJavaLocalDateTime

class ChatSessionDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ChatSessionDAO>(ChatSessions)

    var userId by ChatSessions.userId
    var title by ChatSessions.title
    var createdAt by ChatSessions.createdAt
    var updatedAt by ChatSessions.updatedAt

    fun toModel() = com.example.models.ChatSession(
        id = id.value,
        userId = userId.value,
        title = title,
        createdAt = createdAt.toKotlinLocalDateTime(),
        updatedAt = updatedAt.toKotlinLocalDateTime()
    )
}

class ChatMessageDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ChatMessageDAO>(ChatMessages)

    var sessionId by ChatMessages.sessionId
    var role by ChatMessages.role
    var message by ChatMessages.message
    var createdAt by ChatMessages.createdAt
    var metadata by ChatMessages.metadata

    fun toModel() = com.example.models.ChatMessage(
        id = id.value,
        sessionId = sessionId.value,
        role = role,
        message = message,
        createdAt = createdAt.toKotlinLocalDateTime(),
        metadata = metadata
    )
}

class TempMemoryDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TempMemoryDAO>(TempMemories)

    var sessionId by TempMemories.sessionId
    var content by TempMemories.content
    var messageIds by TempMemories.messageIds
    var createdAt by TempMemories.createdAt
    var importance by TempMemories.importance

    fun toModel() = com.example.models.TempMemory(
        id = id.value,
        sessionId = sessionId.value,
        content = content,
        messageIds = messageIds,
        createdAt = createdAt.toKotlinLocalDateTime(),
        importance = importance
    )
}

class ChatMemoryDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ChatMemoryDAO>(ChatMemories)

    var userId by ChatMemories.userId
    var sessionId by ChatMemories.sessionId
    var summary by ChatMemories.summary
    var embedding by ChatMemories.embedding
    var category by ChatMemories.category
    var importance by ChatMemories.importance
    var createdAt by ChatMemories.createdAt
    var expiresAt by ChatMemories.expiresAt
    var sourceMessageCount by ChatMemories.sourceMessageCount

    fun toModel() = com.example.models.ChatMemory(
        id = id.value,
        userId = userId.value,
        sessionId = sessionId?.value,
        summary = summary,
        embedding = embedding,
        category = category,
        importance = importance,
        createdAt = createdAt.toKotlinLocalDateTime(),
        expiresAt = expiresAt?.toKotlinLocalDateTime(),
        sourceMessageCount = sourceMessageCount
    )
}