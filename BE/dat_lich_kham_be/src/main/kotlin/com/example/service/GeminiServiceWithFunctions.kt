package com.example.service

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.*
import kotlinx.serialization.Serializable

/**
 * Extension của GeminiService với Function Calling support
 * 
 * Service này bọc GeminiService hiện có và thêm khả năng:
 * - Gửi function declarations đến Gemini
 * - Detect function calls từ Gemini response 
 * - Execute functions
 * - Gửi kết quả lại cho Gemini
 */
class GeminiServiceWithFunctions(private val apiKey: String) {
    
    private val client = HttpClient(CIO) {
        engine {
            requestTimeout = 60_000 // 60s cho function calling
        }
    }
    
    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = false
    }
    
    companion object {
        private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta"
        private const val MODEL = "gemini-2.5-flash"
    }
    
    /**
     * Chat with function calling support
     * 
     * @param history Lịch sử conversation (user & model messages)
     * @param userMessage Tin nhắn mới từ user
     * @param onFunctionCall Callback để thực thi function call
     * @return AI response cuối cùng + danh sách functions đã gọi
     */
    suspend fun chatWithFunctions(
        history: List<ConversationMessage>,
        userMessage: String,
        onFunctionCall: suspend (String, Map<String, JsonElement>) -> Any
    ): ChatResponse {
        val functionsExecuted = mutableListOf<String>()
        
        try {
            // 1. Tạo request với function declarations
            val contents = history.map { msg ->
                FunctionContent(
                    role = msg.role,
                    parts = listOf(FunctionPart(text = msg.message))
                )
            } + FunctionContent(
                role = "user",
                parts = listOf(FunctionPart(text = userMessage))
            )
            
            val tools = listOf(
                FunctionTool(
                    functionDeclarations = GeminiFunctions.ALL_FUNCTIONS.map { functionJson ->
                        json.decodeFromString<FunctionDeclaration>(functionJson)
                    }
                )
            )
            
            val systemInstruction = FunctionContent(
                role = "user",
                parts = listOf(FunctionPart(text = """
                    Bạn là trợ lý y tế thông minh tại bệnh viện.
                    
                    NHIỆM VỤ:
                    1. Trả lời câu hỏi sức khỏe của người dùng
                    2. Khi người dùng mô tả triệu chứng, gợi ý khoa phù hợp
                    3. Hỗ trợ đặt lịch khám bệnh hoàn toàn tự động
                    
                    QUY TRÌNH ĐẶT LỊCH:
                    1. Khi user mention triệu chứng hoặc muốn khám:
                       - Gọi get_departments() để xem danh sách khoa
                       - Gợi ý khoa phù hợp dựa trên triệu chứng
                    
                    2. Khi user đồng ý hoặc chọn khoa:
                       - Gọi get_doctors_by_department(department_id)
                       - Hiển thị danh sách bác sĩ với giá khám
                    
                    3. Khi user chọn bác sĩ:
                       - Hỏi ngày muốn khám
                       - Chuyển đổi dd/MM/yyyy sang yyyy-MM-dd
                       - Sau khi có ngày, gọi get_available_time_slots(doctor_id, date)
                       - Hiển thị các khung giờ trống
                    
                    4. Khi user chọn giờ:
                       - Tổng hợp thông tin đặt lịch
                       - Gọi check_user_balance(user_id) để kiểm tra số dư
                       - Nếu đủ tiền, gọi create_appointment()
                    
                    LƯU Ý:
                    - Luôn thân thiện và chuyên nghiệp
                    - Giải thích rõ ràng từng bước
                    - Kiểm tra đầy đủ thông tin trước khi đặt lịch
                    - Không đặt lịch vào Chủ nhật (bệnh viện nghỉ)
                    - QUAN TRỌNG: Phải convert ngày từ dd/MM/yyyy sang yyyy-MM-dd khi gọi functions
                """.trimIndent()))
            )
            
            val requestBody = FunctionCallingRequest(
                contents = contents,
                tools = tools,
                systemInstruction = systemInstruction
            )
            
            // 2. Call Gemini API
            var response = callGeminiAPI(requestBody)
            var attempt = 0
            val maxAttempts = 5 // Giới hạn số lần gọi function
            
            // 3. Handle function calls (có thể có nhiều vòng)
            while (response.candidates?.firstOrNull()?.content?.parts?.any { it.functionCall != null } == true && attempt < maxAttempts) {
                attempt++
                
                // Fix smart cast issue
                val candidates = response.candidates ?: break
                val part = candidates.first().content!!.parts.first { it.functionCall != null }
                val functionCall = part.functionCall!!
                val functionName = functionCall.name
                val args = functionCall.args ?: emptyMap()
                
                println("🔧 AI calling function: $functionName with args: $args")
                
                // Execute function
                val result = onFunctionCall(functionName, args)
                functionsExecuted.add(functionName)
                
                
                // Gửi kết quả lại cho AI
                // Convert result to JsonElement by encoding to JSON string first
                val resultJson = try {
                    // Convert result directly to JsonElement
                    when (result) {
                        is Map<*, *> -> JsonObject(result.mapKeys { it.key.toString() }.mapValues { 
                            when (val v = it.value) {
                                is String -> JsonPrimitive(v)
                                is Number -> JsonPrimitive(v)
                                is Boolean -> JsonPrimitive(v)
                                else -> JsonPrimitive(v.toString())
                            }
                        })
                        is List<*> -> JsonArray(result.map { 
                            when (it) {
                                is String -> JsonPrimitive(it)
                                is Number -> JsonPrimitive(it)
                                is Boolean -> JsonPrimitive(it)
                                else -> JsonPrimitive(it.toString())
                            }
                        })
                        is String -> JsonPrimitive(result)
                        is Number -> JsonPrimitive(result)
                        is Boolean -> JsonPrimitive(result)
                        else -> JsonPrimitive(result.toString())
                    }
                } catch (e: Exception) {
                    println("⚠️ Error converting result to JSON: ${e.message}, using toString()")
                    JsonPrimitive(result.toString())
                }
                
                val functionResponse = FunctionContent(
                    role = "function",
                    parts = listOf(
                        FunctionPart(
                            functionResponse = FunctionResponse(
                                name = functionName,
                                response = buildJsonObject {
                                    put("result", resultJson)
                                }
                            )
                        )
                    )
                )
                
                requestBody.contents = requestBody.contents + functionResponse
                response = callGeminiAPI(requestBody)
            }
            
            // 4. Extract final text response
            val finalText = response.candidates?.firstOrNull()
                ?.content?.parts?.firstOrNull { it.text != null }
                ?.text
                ?: "Xin lỗi, tôi không thể xử lý yêu cầu của bạn lúc này."
            
            return ChatResponse(
                reply = finalText,
                functionsExecuted = functionsExecuted
            )
            
        } catch (e: Exception) {
            println("❌ Error in chatWithFunctions: ${e.message}")
            e.printStackTrace()
            return ChatResponse(
                reply = "Xin lỗi, đã có lỗi xảy ra khi xử lý yêu cầu. Vui lòng thử lại.",
                functionsExecuted = functionsExecuted
            )
        }
    }
    
    /**
     * Helper: Call Gemini API
     */
    private suspend fun callGeminiAPI(requestBody: FunctionCallingRequest): FunctionCallingResponse {
        val response = client.post("$BASE_URL/models/$MODEL:generateContent") {
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            parameter("key", apiKey)
            setBody(json.encodeToString(FunctionCallingRequest.serializer(), requestBody))
        }
        
        if (!response.status.isSuccess()) {
            val body = response.bodyAsText()
            throw Exception("Gemini API error ${response.status}: $body")
        }
        
        val responseBody = response.bodyAsText()
        return json.decodeFromString<FunctionCallingResponse>(responseBody)
    }
    
    fun close() {
        client.close()
    }
}

// ===== Data Classes =====

data class ConversationMessage(
    val role: String, // "user" hoặc "model"
    val message: String
)

data class ChatResponse(
    val reply: String,
    val functionsExecuted: List<String> = emptyList()
)

@Serializable
data class FunctionCallingRequest(
    var contents: List<FunctionContent>,
    val tools: List<FunctionTool>? = null,
    val systemInstruction: FunctionContent? = null
)

@Serializable
data class FunctionContent(
    val role: String,
    val parts: List<FunctionPart>
)

@Serializable
data class FunctionPart(
    val text: String? = null,
    val functionCall: FunctionCall? = null,
    val functionResponse: FunctionResponse? = null
)

@Serializable
data class FunctionCall(
    val name: String,
    val args: Map<String, JsonElement>? = null
)

@Serializable
data class FunctionResponse(
    val name: String,
    val response: JsonObject // Changed from Map<String, Any> to JsonObject
)

@Serializable
data class FunctionTool(
    val functionDeclarations: List<FunctionDeclaration>
)

@Serializable
data class FunctionDeclaration(
    val name: String,
    val description: String,
    val parameters: FunctionParameters? = null
)

@Serializable
data class FunctionParameters(
    val type: String,
    val properties: Map<String, JsonElement>? = null,
    val required: List<String>? = null
)

@Serializable
data class FunctionCallingResponse(
    val candidates: List<FunctionCandidate>? = null
)

@Serializable
data class FunctionCandidate(
    val content: FunctionContent? = null
)
