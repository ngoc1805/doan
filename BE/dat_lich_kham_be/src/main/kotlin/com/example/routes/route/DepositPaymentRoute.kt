package com.example.routes

import com.example.dto.Response.BaseResponse
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

@Serializable
data class DepositPaymentRequest(
    val userId: Int
)

@Serializable
data class RefundDepositRequest(
    val userId: Int
)

fun Route.depositPaymentRoute() {
    val userRepository = UserRepository()
    val transactionRepository = TransactionRepository()

    route("/api/benhnhan") {
        post("/deposit/payment") {
            try {
                val request = call.receive<DepositPaymentRequest>()
                val userId = request.userId
                val depositAmount = 100000 // 100k cố định

                // Wrap toàn bộ logic trong transaction
                val result = org.jetbrains.exposed.sql.transactions.transaction {
                    // Bước 1: Kiểm tra user có tồn tại không
                    val userDao = com.example.dao.UsersDAO.findById(userId)
                    if (userDao == null) {
                        return@transaction mapOf(
                            "status" to HttpStatusCode.NotFound,
                            "success" to false,
                            "message" to "Không tìm thấy thông tin người dùng"
                        )
                    }

                    // Bước 2: Kiểm tra số dư của user
                    val currentBalance = userRepository.getBalanceByUserId(userId)
                    if (currentBalance == null) {
                        return@transaction mapOf(
                            "status" to HttpStatusCode.NotFound,
                            "success" to false,
                            "message" to "Không thể lấy thông tin số dư"
                        )
                    }

                    if (currentBalance < depositAmount) {
                        return@transaction mapOf(
                            "status" to HttpStatusCode.BadRequest,
                            "success" to false,
                            "message" to "Số dư không đủ. Cần: ${depositAmount}đ, Hiện có: ${currentBalance}đ",
                            "requiredAmount" to depositAmount,
                            "currentBalance" to currentBalance,
                            "shortage" to (depositAmount - currentBalance)
                        )
                    }

                    // Bước 3: Trừ tiền từ balance
                    val newBalance = currentBalance - depositAmount
                    val updateSuccess = userRepository.updateBalanceByUserId(
                        userId = userId,
                        balance = newBalance
                    )

                    if (!updateSuccess) {
                        return@transaction mapOf(
                            "status" to HttpStatusCode.InternalServerError,
                            "success" to false,
                            "message" to "Không thể cập nhật số dư"
                        )
                    }

                    println("✓ Đã trừ ${depositAmount}đ từ userId $userId: $currentBalance -> $newBalance")

                    // Bước 4: Tạo bản ghi giao dịch
                    val transactionId = transactionRepository.addTransaction(
                        userId = userId,
                        category = "Cọc và Sử dụng dịch vụ",
                        transactionType = "Cọc tiền lịch khám",
                        amount = depositAmount.toLong(),
                        isIncome = false
                    )

                    if (transactionId == null) {
                        println("✗ Không thể tạo bản ghi giao dịch cho userId $userId")
                    } else {
                        println("✓ Đã tạo giao dịch ID=$transactionId cho userId $userId")
                    }

                    // Trả về kết quả thành công
                    mapOf(
                        "status" to HttpStatusCode.OK,
                        "success" to true,
                        "message" to "Thanh toán tiền cọc thành công",
                        "userId" to userId,
                        "depositAmount" to depositAmount,
                        "previousBalance" to currentBalance,
                        "newBalance" to newBalance,
                        "transactionId" to (transactionId ?: -1)
                    )
                }

                // Xử lý response dựa trên result
                val status = result["status"] as HttpStatusCode
                val success = result["success"] as Boolean
                val message = result["message"] as String

                if (status == HttpStatusCode.OK) {
                    call.respond(
                        status,
                        BaseResponse(
                            success = success,
                            message = message,
                            data = buildJsonObject {
                                put("userId", result["userId"] as Int)
                                put("depositAmount", result["depositAmount"] as Int)
                                put("previousBalance", result["previousBalance"] as Int)
                                put("newBalance", result["newBalance"] as Int)
                                put("transactionId", result["transactionId"] as Int)
                            }
                        )
                    )
                } else if (status == HttpStatusCode.BadRequest && result.containsKey("shortage")) {
                    call.respond(
                        status,
                        BaseResponse(
                            success = success,
                            message = message,
                            data = buildJsonObject {
                                put("requiredAmount", result["requiredAmount"] as Int)
                                put("currentBalance", result["currentBalance"] as Int)
                                put("shortage", result["shortage"] as Int)
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
                println("✗ Lỗi trong quá trình thanh toán tiền cọc: ${e.message}")
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

        // API hoàn tiền cọc
        post("/deposit/refund") {
            try {
                val request = call.receive<RefundDepositRequest>()
                val userId = request.userId
                val refundAmount = 100000 // 100k cố định

                // Wrap toàn bộ logic trong transaction
                val result = org.jetbrains.exposed.sql.transactions.transaction {
                    // Bước 1: Kiểm tra user có tồn tại không
                    val userDao = com.example.dao.UsersDAO.findById(userId)
                    if (userDao == null) {
                        return@transaction mapOf(
                            "status" to HttpStatusCode.NotFound,
                            "success" to false,
                            "message" to "Không tìm thấy thông tin người dùng"
                        )
                    }

                    // Bước 2: Lấy số dư hiện tại
                    val currentBalance = userRepository.getBalanceByUserId(userId)
                    if (currentBalance == null) {
                        return@transaction mapOf(
                            "status" to HttpStatusCode.NotFound,
                            "success" to false,
                            "message" to "Không thể lấy thông tin số dư"
                        )
                    }

                    // Bước 3: Cộng tiền vào balance
                    val newBalance = currentBalance + refundAmount
                    val updateSuccess = userRepository.updateBalanceByUserId(
                        userId = userId,
                        balance = newBalance
                    )

                    if (!updateSuccess) {
                        return@transaction mapOf(
                            "status" to HttpStatusCode.InternalServerError,
                            "success" to false,
                            "message" to "Không thể cập nhật số dư"
                        )
                    }

                    println("✓ Đã hoàn ${refundAmount}đ cho userId $userId: $currentBalance -> $newBalance")

                    // Bước 4: Tạo bản ghi giao dịch
                    val transactionId = transactionRepository.addTransaction(
                        userId = userId,
                        category = "Hoàn tiền",
                        transactionType = "Hoàn tiền khi hủy lịch",
                        amount = refundAmount.toLong(),
                        isIncome = true
                    )

                    if (transactionId == null) {
                        println("✗ Không thể tạo bản ghi giao dịch hoàn tiền cho userId $userId")
                    } else {
                        println("✓ Đã tạo giao dịch hoàn tiền ID=$transactionId cho userId $userId")
                    }

                    // Trả về kết quả thành công
                    mapOf(
                        "status" to HttpStatusCode.OK,
                        "success" to true,
                        "message" to "Hoàn tiền cọc thành công",
                        "userId" to userId,
                        "refundAmount" to refundAmount,
                        "previousBalance" to currentBalance,
                        "newBalance" to newBalance,
                        "transactionId" to (transactionId ?: -1)
                    )
                }

                // Xử lý response dựa trên result
                val status = result["status"] as HttpStatusCode
                val success = result["success"] as Boolean
                val message = result["message"] as String

                if (status == HttpStatusCode.OK) {
                    call.respond(
                        status,
                        BaseResponse(
                            success = success,
                            message = message,
                            data = buildJsonObject {
                                put("userId", result["userId"] as Int)
                                put("refundAmount", result["refundAmount"] as Int)
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
                println("✗ Lỗi trong quá trình hoàn tiền cọc: ${e.message}")
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