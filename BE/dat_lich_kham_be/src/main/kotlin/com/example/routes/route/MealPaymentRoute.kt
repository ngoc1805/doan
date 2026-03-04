package com.example.routes

import com.example.dto.Response.BaseResponse
import com.example.repository.MealRepository
import com.example.repository.TransactionRepository
import com.example.repository.UserRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

// ✅ Thêm data class
@Serializable
data class MealPaymentRequest(
    val userId: Int,
    val cycleId: Int,
    val amount: Int = 500000
)

fun Route.mealPaymentRoute() {
    val userRepository = UserRepository()
    val mealRepository = MealRepository()
    val transactionRepository = TransactionRepository()

    route("/api/benhnhan/payments") {
        post("/meal") {
            try {
                // ✅ FIXED: Thay đổi dòng này
                val body = call.receive<MealPaymentRequest>()
                val userId = body.userId
                val cycleId = body.cycleId
                val amount = body.amount

                println("🔵 [MealPaymentRoute] Received payment request")
                println("   └─ userId: $userId, cycleId: $cycleId, amount: $amount")

                // Phần còn lại giữ nguyên...
                val result = org.jetbrains.exposed.sql.transactions.transaction {
                    println("🔷 [Transaction] Started")

                    // 1. Kiểm tra số dư
                    val currentBalance = userRepository.getBalanceByUserId(userId)
                    println("💰 [Balance Check] Current: $currentBalance")

                    if (currentBalance == null) {
                        println("❌ [Balance Check] User not found")
                        return@transaction mapOf(
                            "status" to HttpStatusCode.NotFound,
                            "success" to false,
                            "message" to "Không tìm thấy thông tin người dùng"
                        )
                    }

                    if (currentBalance < amount) {
                        println("❌ [Balance Check] Insufficient balance")
                        return@transaction mapOf(
                            "status" to HttpStatusCode.BadRequest,
                            "success" to false,
                            "message" to "Số dư không đủ. Cần: ${amount}đ, Hiện có: ${currentBalance}đ"
                        )
                    }

                    // 2. Trừ tiền
                    println("💸 [Deduct] Deducting $amount from balance")
                    val newBalance = currentBalance - amount
                    val updateSuccess = userRepository.updateBalanceByUserId(userId, newBalance)

                    if (!updateSuccess) {
                        println("❌ [Deduct] Failed to update balance")
                        return@transaction mapOf(
                            "status" to HttpStatusCode.InternalServerError,
                            "success" to false,
                            "message" to "Không thể cập nhật số dư"
                        )
                    }
                    println("✅ [Deduct] New balance: $newBalance")

                    // 3. Activate cycle
                    println("🔄 [Activate] Activating cycle $cycleId")
                    val activateSuccess = mealRepository.activateCycle(cycleId)

                    if (!activateSuccess) {
                        println("❌ [Activate] Failed to activate cycle")
                        return@transaction mapOf(
                            "status" to HttpStatusCode.BadRequest,
                            "success" to false,
                            "message" to "Không thể kích hoạt chu kỳ suất ăn"
                        )
                    }
                    println("✅ [Activate] Cycle activated successfully")

                    // 4. Tạo transaction
                    println("📝 [Transaction Record] Creating transaction record")
                    val transactionId = transactionRepository.addTransaction(
                        userId = userId,
                        category = "Thanh toán",
                        transactionType = "Thanh toán suất ăn chu kỳ $cycleId",
                        amount = amount.toLong(),
                        isIncome = false
                    )
                    println("✅ [Transaction Record] Created with ID: $transactionId")

                    println("🔷 [Transaction] Completed successfully")
                    mapOf(
                        "status" to HttpStatusCode.OK,
                        "success" to true,
                        "message" to "Thanh toán thành công",
                        "userId" to userId,
                        "cycleId" to cycleId,
                        "amountPaid" to amount,
                        "previousBalance" to currentBalance,
                        "newBalance" to newBalance,
                        "transactionId" to (transactionId ?: -1)
                    )
                }

                val status = result["status"] as HttpStatusCode
                val success = result["success"] as Boolean
                val message = result["message"] as String

                println("📤 [Response] Sending response with status: $status")

                if (status == HttpStatusCode.OK) {
                    call.respond(
                        status,
                        BaseResponse(
                            success = success,
                            message = message,
                            data = buildJsonObject {
                                put("userId", result["userId"] as Int)
                                put("cycleId", result["cycleId"] as Int)
                                put("amountPaid", result["amountPaid"] as Int)
                                put("previousBalance", result["previousBalance"] as Int)
                                put("newBalance", result["newBalance"] as Int)
                                put("transactionId", result["transactionId"] as Int)
                            }
                        )
                    )
                } else {
                    call.respond(
                        status,
                        BaseResponse(
                            success = success,
                            message = message
                        )
                    )
                }
            } catch (e: Exception) {
                println("💥 [ERROR] Exception occurred: ${e.message}")
                e.printStackTrace()
                call.respond(
                    HttpStatusCode.InternalServerError,
                    BaseResponse(
                        success = false,
                        message = "Lỗi server: ${e.message}"
                    )
                )
            }
        }
    }
}