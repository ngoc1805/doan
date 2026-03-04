package com.example.dat_lich_kham_fe.chatbot

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dat_lich_kham_fe.data.api.address
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import retrofit2.Response
import kotlinx.serialization.Serializable

/**
 * ViewModel đơn giản:
 * - Mỗi user chỉ có 1 session duy nhất
 * - Tự động load lịch sử khi mở app
 * - Không cần quản lý nhiều session
 */
class ChatViewModel(context: Context) : ViewModel() {

    val messageList = mutableStateListOf<MessageModel>()

    var hasMemoryContext by mutableStateOf(false)
        private set

    var isLoadingHistory by mutableStateOf(false)
        private set

    var isLoadingMessage by mutableStateOf(false)
        private set

    private val chatApi: ChatApi
    private val userId = 1 // TODO: Lấy từ user session thực tế

    // Gemini model for fallback
    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.5-flash",  // ✅ Stable, rẻ hơn 2.5-flash
        apiKey = Constants.apiKey
    )

    init {
        // Khởi tạo Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl(address) // Thay đổi theo server của bạn
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        chatApi = retrofit.create(ChatApi::class.java)

        // Load lịch sử ngay khi khởi tạo
        loadChatHistory()
    }

    /**
     * Load toàn bộ lịch sử chat của user
     */
    private fun loadChatHistory() {
        viewModelScope.launch {
            isLoadingHistory = true

            try {
                // Gọi API để load lịch sử
                val response = chatApi.getHistory(
                    userId = userId,
                    limit = 1000 // Load tất cả messages
                )

                if (response.isSuccessful && response.body() != null) {
                    val historyResponse = response.body()!!

                    // Clear messages cũ
                    messageList.clear()

                    // Thêm tất cả messages từ history
                    historyResponse.messages.forEach { msg ->
                        messageList.add(
                            MessageModel(
                                id = msg.id,
                                message = msg.message,
                                role = msg.role
                            )
                        )
                    }

                    // Cập nhật context flag
                    hasMemoryContext = historyResponse.messages.isNotEmpty()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                println("Load history error: ${e.message}")
            } finally {
                isLoadingHistory = false
            }
        }
    }

    /**
     * Gửi tin nhắn
     */
    fun sendMessage(question: String) {
        if (question.isBlank()) return

        // 1. Thêm tin nhắn user vào messageList ngay lập tức (dùng ID tạm)
        val tempUserId = System.currentTimeMillis().toInt()
        messageList.add(
            MessageModel(
                id = tempUserId,
                message = question,
                role = "user"
            )
        )

        isLoadingMessage = true

        viewModelScope.launch {
            try {
                val response = chatApi.sendMessage(
                    SendMessageRequest(
                        userId = userId,
                        message = question
                    )
                )

                if (response.isSuccessful && response.body() != null) {
                    val sendResponse = response.body()!!

                    // Update context flag nếu có functions được gọi
                    hasMemoryContext = sendResponse.functionsExecuted.isNotEmpty()

                    // Log functions executed (for debugging)
                    if (sendResponse.functionsExecuted.isNotEmpty()) {
                        println("✅ AI executed: ${sendResponse.functionsExecuted.joinToString(", ")}")
                    }

                    // Thêm message bot vào messageList
                    messageList.add(
                        MessageModel(
                            id = sendResponse.botMessageId,
                            message = sendResponse.reply,
                            role = "model"
                        )
                    )
                } else {
                    sendMessageWithLocalGemini(question)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                sendMessageWithLocalGemini(question)
            } finally {
                isLoadingMessage = false
            }
        }
    }

    /**
     * Fallback: Gửi tin nhắn với Gemini local
     */
    private suspend fun sendMessageWithLocalGemini(question: String) {
        try {
            val chat = generativeModel.startChat(
                history = messageList.dropLast(1).map {
                    content(it.role) { text(it.message) }
                }.toList()
            )

            val response = chat.sendMessage(question)

            // Tạo id tạm thời cho message local khi FE fallback
            messageList.add(
                MessageModel(
                    id = System.currentTimeMillis().toInt(),
                    message = response.text ?: "Xin lỗi, tôi không thể trả lời.",
                    role = "model"
                )
            )

            hasMemoryContext = false
        } catch (e: Exception) {
            messageList.add(
                MessageModel(
                    id = System.currentTimeMillis().toInt(),
                    message = "Lỗi: ${e.message}",
                    role = "model"
                )
            )
        }
    }

    /**
     * Làm mới lịch sử (pull to refresh)
     */
    fun refreshHistory() {
        loadChatHistory()
    }

    /**
     * Xóa toàn bộ chat (nếu cần)
     * Lưu ý: Chỉ xóa local, không xóa trên server
     */
    fun clearLocalChat() {
        messageList.clear()
        hasMemoryContext = false
    }
}

// ===== API Interface =====

interface ChatApi {

    @GET("api/chat/history")
    suspend fun getHistory(
        @Query("userId") userId: Int,
        @Query("limit") limit: Int = 1000
    ): Response<ChatHistoryResponse>

    @POST("api/ai-chat/send")  // ✅ AI endpoint với function calling
    suspend fun sendMessage(
        @Body request: SendMessageRequest
    ): Response<AISendMessageResponse>
}

// ===== Data Models =====

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

// AI Chat Response (mới)
@Serializable
data class AISendMessageResponse(
    val sessionId: Int,
    val reply: String,
    val functionsExecuted: List<String> = emptyList(),
    val userMessageId: Int,
    val botMessageId: Int
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