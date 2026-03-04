package com.example.models

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class ChatSession(
    val id: Int,
    val userId: Int,
    val title: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

@Serializable
data class ChatMessage(
    val id: Int,
    val sessionId: Int,
    val role: String,
    val message: String,
    val createdAt: LocalDateTime,
    val metadata: String?
)

@Serializable
data class TempMemory(
    val id: Int,
    val sessionId: Int,
    val content: String,
    val messageIds: String,
    val createdAt: LocalDateTime,
    val importance: Int
)

@Serializable
data class ChatMemory(
    val id: Int,
    val userId: Int,
    val sessionId: Int?,
    val summary: String,
    val embedding: String,
    val category: String?,
    val importance: Int,
    val createdAt: LocalDateTime,
    val expiresAt: LocalDateTime?,
    val sourceMessageCount: Int
)