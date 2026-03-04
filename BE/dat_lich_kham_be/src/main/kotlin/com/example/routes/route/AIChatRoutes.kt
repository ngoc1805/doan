package com.example.routes.route

import com.example.service.*
import com.example.repository.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*

/**
 * AI Chat Routes với Function Calling
 *
 * Endpoint mới cho AI thông minh có khả năng:
 * - Tự động gọi functions để lấy dữ liệu
 * - Đặt lịch khám bệnh tự động
 * - Nhớ context và xử lý multi-turn conversations
 */
fun Route.aiChatRoutes(
    chatService: ChatService,
    aiService: GeminiServiceWithFunctions,
    bookingExecutor: BookingFunctionExecutor
) {

    route("/api/ai-chat") {

        /**
         * POST /api/ai-chat/send
         * Gửi tin nhắn đến AI thông minh với function calling
         * Body: { userId, message }
         */
        post("/send") {
            try {
                val request = call.receive<AISendMessageRequest>()

                println("📨 AI Chat Request from user ${request.userId}: ${request.message}")

                // 1. Lấy hoặc tạo session
                val session = chatService.getOrCreateUserSession(request.userId)

                // 2. Lưu tin nhắn user
                val userMessage = chatService.saveMessage(
                    sessionId = session.id,
                    role = "user",
                    message = request.message
                )

                // 3. Lấy lịch sử conversation
                val history = chatService.getChatHistory(request.userId, limit = 20).messages
                    .filter { it.role in listOf("user", "model") }
                    .map { ConversationMessage(role = it.role, message = it.message) }

                println("📚 Loaded ${history.size} messages from history")

                // 4. Chat với AI (có function calling)
                val aiResponse = aiService.chatWithFunctions(
                    history = history,
                    userMessage = request.message,
                    onFunctionCall = { functionName, params ->
                        println("🔧 Executing function: $functionName")

                        // Convert JsonElement params to Map<String, Any?>
                        val convertedParams = params.mapValues { (_, value) ->
                            when (value) {
                                is JsonPrimitive -> {
                                    when {
                                        value.isString -> value.content
                                        value.booleanOrNull != null -> value.boolean
                                        value.intOrNull != null -> value.int
                                        value.longOrNull != null -> value.long
                                        value.doubleOrNull != null -> value.double
                                        else -> value.content
                                    }
                                }
                                is JsonObject -> value.toString()
                                is JsonArray -> value.toString()
                                else -> value.toString()
                            }
                        }.toMutableMap()

                        // Inject userId vào params nếu function cần
                        if (functionName in listOf("create_appointment", "check_user_balance")) {
                            convertedParams["user_id"] = request.userId
                        }

                        // Execute function
                        val result = bookingExecutor.execute(functionName, convertedParams)
                        println("✅ Function $functionName result: $result")

                        result
                    }
                )

                // 5. Lưu response của AI
                val botMessage = chatService.saveMessage(
                    sessionId = session.id,
                    role = "model",
                    message = aiResponse.reply
                )

                // 6. Detect và lưu temp memory (optional)
                chatService.detectAndSaveTempMemory(
                    sessionId = session.id,
                    messages = listOf(userMessage, botMessage)
                )

                // 7. Trả về response
                call.respond(
                    HttpStatusCode.OK,
                    AISendMessageResponse(
                        sessionId = session.id,
                        reply = aiResponse.reply,
                        functionsExecuted = aiResponse.functionsExecuted,
                        userMessageId = userMessage.id,
                        botMessageId = botMessage.id
                    )
                )

                println("✨ AI Response sent successfully (${aiResponse.functionsExecuted.size} functions called)")

            } catch (e: Exception) {
                println("❌ Error in /ai-chat/send: ${e.message}")
                e.printStackTrace()

                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf(
                        "error" to "Failed to process AI chat: ${e.message}"
                    )
                )
            }
        }

        /**
         * GET /api/ai-chat/history
         * Lấy lịch sử chat (giống như endpoint cũ nhưng dùng cho AI chat)
         */
        get("/history") {
            try {
                val userId = call.request.queryParameters["userId"]?.toIntOrNull()
                val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 100

                if (userId == null) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "userId is required"))
                    return@get
                }

                val history = chatService.getChatHistory(userId, limit)

                call.respond(HttpStatusCode.OK, mapOf(
                    "sessionId" to (chatService.getOrCreateUserSession(userId).id),
                    "messages" to history.messages
                ))

            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("error" to "Failed to load history: ${e.message}")
                )
            }
        }
    }
}

// ===== Request/Response Models =====

@Serializable
data class AISendMessageRequest(
    val userId: Int,
    val message: String
)

@Serializable
data class AISendMessageResponse(
    val sessionId: Int,
    val reply: String,
    val functionsExecuted: List<String> = emptyList(),
    val userMessageId: Int,
    val botMessageId: Int
)
