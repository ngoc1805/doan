package com.example.service

import com.example.dao.*
import com.example.models.*
import com.example.Tables.*
import com.example.routes.ChatHistoryResponse
import com.example.routes.MessageResponse
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SortOrder
import kotlinx.datetime.*
import kotlinx.serialization.json.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.LocalDateTime as JavaLocalDateTime

/**
 * ChatService - Mỗi user chỉ có 1 session duy nhất
 * GIỮ NGUYÊN TẤT CẢ TÍNH NĂNG: memory, embedding, context, etc.
 */
class ChatService {

    companion object {
        const val RECENT_MESSAGES_LIMIT = 20
        const val IMPORTANT_THRESHOLD = 7
        const val MAX_MEMORIES_TO_RETRIEVE = 5
    }

    /**
     * Lấy hoặc tạo session duy nhất của user
     * Mỗi user chỉ có 1 session, không bao giờ tạo nhiều session
     */
    fun getOrCreateUserSession(userId: Int): ChatSession = transaction {
        // Tìm session của user (chỉ có 1)
        val existing = ChatSessionDAO.find {
            ChatSessions.userId eq userId
        }.firstOrNull()

        existing?.toModel() ?: run {
            // Tạo session mới nếu chưa có
            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            val javaTime = now.toJavaLocalDateTime()

            ChatSessionDAO.new {
                this.userId = UsersDAO[userId].id
                this.title = "Chat với Bot"
                this.createdAt = javaTime
                this.updatedAt = javaTime
            }.toModel()
        }
    }

    /**
     * Lưu tin nhắn mới
     */
    fun saveMessage(sessionId: Int, role: String, message: String, metadata: String? = null): ChatMessage = transaction {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).toJavaLocalDateTime()

        // Lưu message
        val chatMessage = ChatMessageDAO.new {
            this.sessionId = ChatSessionDAO[sessionId].id
            this.role = role
            this.message = message
            this.createdAt = now
            this.metadata = metadata
        }

        // Update session updatedAt
        val session = ChatSessionDAO[sessionId]
        session.updatedAt = now

        chatMessage.toModel()
    }

    /**
     * Lấy toàn bộ lịch sử chat của user
     * Trả về tất cả messages từ session duy nhất của user
     */
    fun getChatHistory(userId: Int, limit: Int = 1000): ChatHistoryResponse = transaction {
        // Lấy session duy nhất của user
        val session = getOrCreateUserSession(userId)

        // Lấy tất cả messages của user (từ cũ đến mới)
        val messages = ChatMessageDAO.find {
            ChatMessages.sessionId eq session.id
        }.orderBy(ChatMessages.createdAt to SortOrder.ASC)
            .limit(limit)
            .map { msg ->
                MessageResponse(
                    id = msg.id.value,
                    role = msg.role,
                    message = msg.message,
                    createdAt = msg.createdAt.toString()
                )
            }

        ChatHistoryResponse(
            sessions = emptyList(), // Không cần danh sách sessions nữa
            messages = messages
        )
    }

    /**
     * Lấy context để trả lời (tin nhắn gần + memories)
     */
    fun getConversationContext(userId: Int, sessionId: Int, userMessage: String): ConversationContext = transaction {
        // 1. Lấy tin nhắn gần nhất
        val recentMessages = ChatMessageDAO.find {
            ChatMessages.sessionId eq sessionId
        }.orderBy(ChatMessages.createdAt to SortOrder.DESC)
            .limit(RECENT_MESSAGES_LIMIT)
            .map { it.toModel() }
            .reversed()

        // 2. Lấy memories liên quan (dựa vào similarity với user message)
        val relevantMemories = getRelevantMemories(userId, userMessage)

        // 3. Lấy temp memories của session hiện tại
        val tempMemories = TempMemoryDAO.find {
            TempMemories.sessionId eq sessionId
        }.map { it.toModel() }

        ConversationContext(
            recentMessages = recentMessages,
            relevantMemories = relevantMemories,
            tempMemories = tempMemories
        )
    }

    /**
     * Phát hiện và lưu thông tin quan trọng tạm thời
     */
    fun detectAndSaveTempMemory(sessionId: Int, messages: List<ChatMessage>): TempMemory? = transaction {
        val importantInfo = extractImportantInfo(messages)

        if (importantInfo != null && importantInfo.importance >= IMPORTANT_THRESHOLD) {
            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).toJavaLocalDateTime()
            val messageIds = Json.encodeToString(messages.map { it.id })

            TempMemoryDAO.new {
                this.sessionId = ChatSessionDAO[sessionId].id
                this.content = importantInfo.content
                this.messageIds = messageIds
                this.createdAt = now
                this.importance = importantInfo.importance
            }.toModel()
        } else null
    }

    /**
     * Batch job: Tóm tắt và tạo embedding cuối ngày
     */
    suspend fun consolidateDailyMemories(userId: Int, date: LocalDate) = newSuspendedTransaction {
        val session = getOrCreateUserSession(userId)

        val messages = ChatMessageDAO.find {
            ChatMessages.sessionId eq session.id
        }.filter {
            it.createdAt.toLocalDate() == date.toJavaLocalDate()
        }.map { it.toModel() }

        if (messages.size >= 5) {
            val summary = summarizeConversation(messages)
            val embedding = createEmbedding(summary.text)

            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            val javaTime = now.toJavaLocalDateTime()

            ChatMemoryDAO.new {
                this.userId = UsersDAO[userId].id
                this.sessionId = ChatSessionDAO[session.id].id
                this.summary = summary.text
                this.embedding = Json.encodeToString(embedding)
                this.category = summary.category
                this.importance = summary.importance
                this.createdAt = javaTime
                this.expiresAt = summary.expiresAt?.toJavaLocalDateTime()
                this.sourceMessageCount = messages.size
            }

            // Xóa temp memories đã xử lý
            TempMemoryDAO.find {
                TempMemories.sessionId eq session.id
            }.forEach { it.delete() }
        }
    }

    /**
     * Tìm memories liên quan (similarity search)
     */
    private fun getRelevantMemories(userId: Int, query: String): List<ChatMemory> = transaction {
        val queryEmbedding = createEmbedding(query)

        val allMemories = ChatMemoryDAO.find {
            ChatMemories.userId eq userId
        }.map {
            val memory = it.toModel()
            val memoryEmbedding = Json.decodeFromString<List<Float>>(memory.embedding)
            val similarity = cosineSimilarity(queryEmbedding, memoryEmbedding)
            memory to similarity
        }

        allMemories
            .sortedByDescending { it.second }
            .take(MAX_MEMORIES_TO_RETRIEVE)
            .map { it.first }
    }

    /**
     * Extract thông tin quan trọng từ messages
     */
    private fun extractImportantInfo(messages: List<ChatMessage>): ImportantInfo? {
        val text = messages.joinToString(" ") { it.message }

        val patterns = mapOf(
            "health_symptom" to listOf("đau", "sốt", "ho", "mệt", "chóng mặt", "buồn nôn"),
            "appointment" to listOf("đặt lịch", "hẹn khám", "khám bệnh", "ngày mai", "tuần sau"),
            "personal_info" to listOf("tên tôi", "sinh năm", "địa chỉ", "số điện thoại"),
            "medical_history" to listOf("bệnh sử", "dị ứng", "đang dùng thuốc", "tiền sử")
        )

        var maxImportance = 0
        var category = ""

        patterns.forEach { (cat, keywords) ->
            val matches = keywords.count { keyword ->
                text.lowercase().contains(keyword)
            }
            if (matches > 0) {
                val importance = minOf(10, matches * 3)
                if (importance > maxImportance) {
                    maxImportance = importance
                    category = cat
                }
            }
        }

        return if (maxImportance >= IMPORTANT_THRESHOLD) {
            ImportantInfo(
                content = text,
                importance = maxImportance,
                category = category
            )
        } else null
    }

    /**
     * Tóm tắt conversation (sẽ gọi AI)
     */
    private suspend fun summarizeConversation(messages: List<ChatMessage>): ConversationSummary {
        val text = messages.joinToString("\n") {
            "${it.role}: ${it.message}"
        }

        return ConversationSummary(
            text = "User hỏi về triệu chứng sốt và đau đầu. Bot khuyên nên uống nhiều nước và nghỉ ngơi.",
            category = "health_symptom",
            importance = 8,
            expiresAt = null
        )
    }

    /**
     * Tạo embedding vector (sẽ gọi AI)
     */
    private fun createEmbedding(text: String): List<Float> {
        return List(768) { kotlin.random.Random.nextFloat() }
    }

    /**
     * Tính cosine similarity
     */
    private fun cosineSimilarity(vec1: List<Float>, vec2: List<Float>): Float {
        require(vec1.size == vec2.size)

        val dotProduct = vec1.zip(vec2).sumOf { (a, b) -> (a * b).toDouble() }
        val norm1 = kotlin.math.sqrt(vec1.sumOf { (it * it).toDouble() })
        val norm2 = kotlin.math.sqrt(vec2.sumOf { (it * it).toDouble() })

        return (dotProduct / (norm1 * norm2)).toFloat()
    }
}

// Data classes
data class ConversationContext(
    val recentMessages: List<ChatMessage>,
    val relevantMemories: List<ChatMemory>,
    val tempMemories: List<TempMemory>
)

data class ImportantInfo(
    val content: String,
    val importance: Int,
    val category: String
)

data class ConversationSummary(
    val text: String,
    val category: String?,
    val importance: Int,
    val expiresAt: LocalDateTime?
)