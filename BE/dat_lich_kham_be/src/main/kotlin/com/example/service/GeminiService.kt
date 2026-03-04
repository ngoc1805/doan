package com.example.service

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.*
import kotlinx.serialization.Serializable

/**
 * Service để tương tác với Gemini API
 * Hỗ trợ: Text generation, Embedding, Summarization
 */
class GeminiService(private val apiKey: String) {

    private val client = HttpClient(CIO) {
        engine {
            requestTimeout = 30_000
        }
    }

    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = false
    }

    companion object {
        private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta"
        private const val GENERATE_MODEL = "gemini-2.5-flash"
        private const val EMBEDDING_MODEL = "text-embedding-004"
    }

    /**
     * Generate text response từ Gemini
     */
    suspend fun generateResponse(prompt: String): String {
        return try {
            val requestBody = GeminiRequest(
                contents = listOf(
                    Content(
                        parts = listOf(Part(text = prompt))
                    )
                )
            )

            val response = client.post("$BASE_URL/models/$GENERATE_MODEL:generateContent") {
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                parameter("key", apiKey)
                setBody(Json.encodeToString(GeminiRequest.serializer(), requestBody))
            }

            if (!response.status.isSuccess()) {
                throw Exception("Gemini API error: ${response.status}")
            }

            val responseBody = response.bodyAsText()
            val geminiResponse = json.decodeFromString<GeminiResponse>(responseBody)

            geminiResponse.candidates?.firstOrNull()
                ?.content?.parts?.firstOrNull()
                ?.text
                ?: "Không có phản hồi từ AI"

        } catch (e: Exception) {
            println("Error calling Gemini API: ${e.message}")
            e.printStackTrace()
            "Xin lỗi, đã có lỗi xảy ra khi xử lý yêu cầu của bạn."
        }
    }

    /**
     * Tạo embedding vector cho text
     * Returns: List of 768 floats
     */
    suspend fun createEmbedding(text: String): List<Float> {
        return try {
            val requestBody = EmbeddingRequest(
                content = Content(
                    parts = listOf(Part(text = text))
                )
            )

            val response = client.post("$BASE_URL/models/$EMBEDDING_MODEL:embedContent") {
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                parameter("key", apiKey)
                setBody(Json.encodeToString(EmbeddingRequest.serializer(), requestBody))
            }

            if (!response.status.isSuccess()) {
                throw Exception("Embedding API error: ${response.status}")
            }

            val responseBody = response.bodyAsText()
            val embeddingResponse = json.decodeFromString<EmbeddingResponse>(responseBody)

            embeddingResponse.embedding?.values ?: emptyList()

        } catch (e: Exception) {
            println("Error creating embedding: ${e.message}")
            e.printStackTrace()
            // Return empty embedding on error
            emptyList()
        }
    }

    /**
     * Tóm tắt conversation thành 1-2 câu ngắn gọn
     */
    suspend fun summarizeConversation(conversationText: String): String {
        val prompt = """
            Hãy tóm tắt cuộc hội thoại sau đây thành 1-2 câu ngắn gọn.
            Chỉ giữ lại các facts quan trọng, bỏ qua phần chào hỏi và lịch sự.
            
            NGUYÊN TẮC:
            - Tóm tắt phải ngắn gọn (tối đa 100 từ)
            - Chỉ giữ thông tin thực sự quan trọng
            - Không thêm ý kiến chủ quan
            - Viết ở dạng câu trần thuật
            
            CUỘC HỘI THOẠI:
            $conversationText
            
            TÓM TẮT (chỉ trả về bản tóm tắt, không cần giải thích):
        """.trimIndent()

        return generateResponse(prompt)
    }

    /**
     * Detect category từ conversation
     */
    suspend fun detectCategory(conversationText: String): String? {
        val prompt = """
            Phân loại cuộc hội thoại sau vào MỘT trong các category sau:
            - health_symptom: Nói về triệu chứng, bệnh tật
            - appointment: Đặt lịch, hẹn khám
            - medical_history: Tiền sử bệnh, dị ứng, thuốc đang dùng
            - personal_info: Thông tin cá nhân (tên, tuổi, địa chỉ)
            - general: Các chủ đề chung khác
            
            CUỘC HỘI THOẠI:
            $conversationText
            
            Chỉ trả về TÊN CATEGORY (ví dụ: health_symptom), không giải thích:
        """.trimIndent()

        val response = generateResponse(prompt).trim().lowercase()

        return when {
            response.contains("health_symptom") -> "health_symptom"
            response.contains("appointment") -> "appointment"
            response.contains("medical_history") -> "medical_history"
            response.contains("personal_info") -> "personal_info"
            else -> "general"
        }
    }

    /**
     * Calculate importance score (1-10) cho conversation
     */
    suspend fun calculateImportance(conversationText: String): Int {
        val prompt = """
            Đánh giá mức độ quan trọng của cuộc hội thoại sau trên thang điểm 1-10:
            - 1-3: Chào hỏi, tán gẫu, không quan trọng
            - 4-6: Thông tin hữu ích nhưng không cấp thiết
            - 7-8: Thông tin y tế quan trọng, triệu chứng cụ thể
            - 9-10: Thông tin cực kỳ quan trọng, cấp cứu, quyết định y tế
            
            CUỘC HỘI THOẠI:
            $conversationText
            
            Chỉ trả về MỘT SỐ từ 1-10, không giải thích:
        """.trimIndent()

        val response = generateResponse(prompt).trim()

        return response.filter { it.isDigit() }
            .firstOrNull()
            ?.toString()
            ?.toIntOrNull()
            ?: 5 // Default medium importance
    }

    /**
     * Generate response với context đầy đủ
     */
    suspend fun generateWithContext(
        userMessage: String,
        recentMessages: List<String>,
        relevantMemories: List<String>,
        tempMemories: List<String>
    ): String {
        val contextBuilder = StringBuilder()

        contextBuilder.appendLine("Bạn là trợ lý AI y tế thông minh và thân thiện.")
        contextBuilder.appendLine("Hãy trả lời câu hỏi của người dùng dựa trên:")
        contextBuilder.appendLine()

        // Add memories if available
        if (relevantMemories.isNotEmpty()) {
            contextBuilder.appendLine("THÔNG TIN ĐÃ LƯU TỪ TRƯỚC:")
            relevantMemories.forEach { memory ->
                contextBuilder.appendLine("- $memory")
            }
            contextBuilder.appendLine()
        }

        // Add temp memories if available
        if (tempMemories.isNotEmpty()) {
            contextBuilder.appendLine("THÔNG TIN QUAN TRỌNG TRONG PHIÊN:")
            tempMemories.forEach { temp ->
                contextBuilder.appendLine("- $temp")
            }
            contextBuilder.appendLine()
        }

        // Add recent conversation
        if (recentMessages.isNotEmpty()) {
            contextBuilder.appendLine("CUỘC HỘI THOẠI GẦN ĐÂY:")
            recentMessages.takeLast(10).forEach { msg ->
                contextBuilder.appendLine(msg)
            }
            contextBuilder.appendLine()
        }

        // Add current question
        contextBuilder.appendLine("CÂU HỎI HIỆN TẠI:")
        contextBuilder.appendLine(userMessage)
        contextBuilder.appendLine()
        contextBuilder.appendLine("Hãy trả lời ngắn gọn, chính xác và thân thiện.")

        return generateResponse(contextBuilder.toString())
    }

    /**
     * Close HTTP client khi không dùng nữa
     */
    fun close() {
        client.close()
    }
}

// ===== Data Classes for API =====

@Serializable
data class GeminiRequest(
    val contents: List<Content>
)

@Serializable
data class Content(
    val parts: List<Part>
)

@Serializable
data class Part(
    val text: String
)

@Serializable
data class GeminiResponse(
    val candidates: List<Candidate>? = null
)

@Serializable
data class Candidate(
    val content: Content? = null
)

@Serializable
data class EmbeddingRequest(
    val content: Content
)

@Serializable
data class EmbeddingResponse(
    val embedding: Embedding? = null
)

@Serializable
data class Embedding(
    val values: List<Float>
)