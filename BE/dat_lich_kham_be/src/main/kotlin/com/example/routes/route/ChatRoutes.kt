package com.example.routes

import com.example.service.ChatService
import com.example.service.GeminiService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

fun Route.chatRoutes(chatService: ChatService, geminiService: GeminiService) {

    route("/api/chat") {

        /**
         * GET /api/chat/history
         * Lấy toàn bộ lịch sử chat của user
         * Query params: userId, limit (optional)
         */
        get("/history") {
            try {
                val userId = call.request.queryParameters["userId"]?.toIntOrNull()
                val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 1000

                if (userId == null) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "userId is required"))
                    return@get
                }

                // Lấy toàn bộ lịch sử
                val history = chatService.getChatHistory(userId, limit)

                call.respond(HttpStatusCode.OK, history)

            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("error" to "Failed to load history: ${e.message}")
                )
            }
        }

        /**
         * POST /api/chat/send
         * Gửi tin nhắn và nhận phản hồi
         * Body: { userId, message }
         */
        post("/send") {
            try {
                val request = call.receive<SendMessageRequest>()

                // 1. Lấy hoặc tạo session duy nhất của user
                val session = chatService.getOrCreateUserSession(request.userId)

                // 2. Lưu tin nhắn của user
                val userMessage = chatService.saveMessage(
                    sessionId = session.id,
                    role = "user",
                    message = request.message
                )

                // 3. Lấy context để trả lời
                val context = chatService.getConversationContext(
                    userId = request.userId,
                    sessionId = session.id,
                    userMessage = request.message
                )

                // 4. Tạo prompt với context
                val recentMessages = context.recentMessages.map {
                    "${it.role}: ${it.message}"
                }
                val relevantMemories = context.relevantMemories.map { it.summary }
                val tempMemories = context.tempMemories.map { it.content }

                // 5. Gọi Gemini để tạo response
                val botResponse = geminiService.generateWithContext(
                    userMessage = request.message,
                    recentMessages = recentMessages,
                    relevantMemories = relevantMemories,
                    tempMemories = tempMemories
                )

                // 6. Lưu response của bot
                val botMessage = chatService.saveMessage(
                    sessionId = session.id,
                    role = "model",
                    message = botResponse
                )

                // 7. Detect và lưu temp memory nếu cần
                chatService.detectAndSaveTempMemory(
                    sessionId = session.id,
                    messages = listOf(userMessage, botMessage)
                )

                // 8. Trả về response
                call.respond(
                    HttpStatusCode.OK,
                    SendMessageResponse(
                        sessionId = session.id,
                        userMessage = MessageResponse(
                            id = userMessage.id,
                            role = userMessage.role,
                            message = userMessage.message,
                            createdAt = userMessage.createdAt.toString()
                        ),
                        botMessage = MessageResponse(
                            id = botMessage.id,
                            role = botMessage.role,
                            message = botMessage.message,
                            createdAt = botMessage.createdAt.toString()
                        ),
                        context = ContextResponse(
                            recentMessagesCount = context.recentMessages.size,
                            relevantMemoriesCount = context.relevantMemories.size,
                            hasContext = context.recentMessages.isNotEmpty() || context.relevantMemories.isNotEmpty()
                        )
                    )
                )

            } catch (e: Exception) {
                e.printStackTrace()
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("error" to "Failed to send message: ${e.message}")
                )
            }
        }
    }
}

// ===== Request & Response Models =====

@Serializable
data class SendMessageRequest(
    val userId: Int,
    val message: String
)

@Serializable
data class SendMessageResponse(
    val sessionId: Int,
    val userMessage: MessageResponse,
    val botMessage: MessageResponse,
    val context: ContextResponse
)

@Serializable
data class MessageResponse(
    val id: Int,
    val role: String,
    val message: String,
    val createdAt: String
)

@Serializable
data class ContextResponse(
    val recentMessagesCount: Int,
    val relevantMemoriesCount: Int,
    val hasContext: Boolean
)

@Serializable
data class ChatHistoryResponse(
    val sessions: List<SessionSummary>,
    val messages: List<MessageResponse>
)

@Serializable
data class SessionSummary(
    val id: Int,
    val title: String,
    val lastMessage: String?,
    val messageCount: Int,
    val createdAt: String,
    val isActive: Boolean
)