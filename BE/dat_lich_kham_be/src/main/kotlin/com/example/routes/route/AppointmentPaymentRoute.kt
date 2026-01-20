package com.example.routes

import com.example.dto.Request.PaymentRequest
import com.example.dto.Response.BaseResponse
import com.example.repository.AppointmentRepository
import com.example.repository.PaymentRepository
import com.example.repository.TransactionRepository
import com.example.repository.UserRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put



fun Route.appointmentPaymentRoute() {
    val userRepository = UserRepository()
    val appointmentRepository = AppointmentRepository()
    val paymentRepository = PaymentRepository()
    val transactionRepository = TransactionRepository()



    route("/api/benhnhan"){
        post("/appointment/payment") {
            try {
                val request = call.receive<PaymentRequest>()
                val appointmentId = request.appointmentId

                // Wrap toàn bộ logic trong transaction
                val result = org.jetbrains.exposed.sql.transactions.transaction {
                    // Bước 1: Lấy thông tin appointment trực tiếp từ DAO
                    val appointmentDao = com.example.dao.AppointmentsDAO.findById(appointmentId)
                    if (appointmentDao == null) {
                        return@transaction mapOf(
                            "status" to HttpStatusCode.NotFound,
                            "success" to false,
                            "message" to "Không tìm thấy thông tin lịch hẹn"
                        )
                    }

                    // KIỂM TRA TRẠNG THÁI APPOINTMENT
                    val currentStatus = appointmentDao.status
                    val invalidStatusesForPayment = listOf( "Đã thanh toán", "Đã hoàn tất", "Đã hủy")

                    if (currentStatus in invalidStatusesForPayment) {
                        return@transaction mapOf(
                            "status" to HttpStatusCode.BadRequest,
                            "success" to false,
                            "message" to "Không thể thanh toán lịch hẹn với trạng thái: $currentStatus",
                            "appointmentId" to appointmentId,
                            "currentStatus" to currentStatus
                        )
                    }

                    val userId = appointmentDao.userId.value

                    // Bước 2: Tính tổng tiền phải trả
                    val totalAmount = paymentRepository.calculateTotalPayment(appointmentId)
                    if (totalAmount == null) {
                        return@transaction mapOf(
                            "status" to HttpStatusCode.BadRequest,
                            "success" to false,
                            "message" to "Không thể tính toán số tiền thanh toán"
                        )
                    }

                    // Bước 3: Kiểm tra số dư của user
                    val currentBalance = userRepository.getBalanceByUserId(userId)
                    if (currentBalance == null) {
                        return@transaction mapOf(
                            "status" to HttpStatusCode.NotFound,
                            "success" to false,
                            "message" to "Không tìm thấy thông tin người dùng"
                        )
                    }

                    if (currentBalance < totalAmount) {
                        return@transaction mapOf(
                            "status" to HttpStatusCode.BadRequest,
                            "success" to false,
                            "message" to "Số dư không đủ. Cần: ${totalAmount}đ, Hiện có: ${currentBalance}đ",
                            "requiredAmount" to totalAmount,
                            "currentBalance" to currentBalance,
                            "shortage" to (totalAmount - currentBalance)
                        )
                    }

                    // Bước 4: Trừ tiền từ balance
                    val newBalance = currentBalance - totalAmount
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

                    println("✓ Đã trừ ${totalAmount}đ từ userId $userId: $currentBalance -> $newBalance")

                    // Bước 5: Tạo bản ghi giao dịch
                    val transactionId = transactionRepository.addTransaction(
                        userId = userId,
                        category = "Cọc và Sử dụng dịch vụ",
                        transactionType = "Thanh toán cho lịch khám số $appointmentId",
                        amount = totalAmount.toLong(),
                        isIncome = false
                    )

                    if (transactionId == null) {
                        println("✗ Không thể tạo bản ghi giao dịch cho appointmentId $appointmentId")
                    } else {
                        println("✓ Đã tạo giao dịch ID=$transactionId cho appointmentId $appointmentId")
                    }

                    // Trả về kết quả thành công
                    mapOf(
                        "status" to HttpStatusCode.OK,
                        "success" to true,
                        "message" to "Thanh toán thành công",
                        "appointmentId" to appointmentId,
                        "userId" to userId,
                        "amountPaid" to totalAmount,
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
                                put("appointmentId", result["appointmentId"] as Int)
                                put("userId", result["userId"] as Int)
                                put("amountPaid", result["amountPaid"] as Int)
                                put("previousBalance", result["previousBalance"] as Int)
                                put("newBalance", result["newBalance"] as Int)
                                put("transactionId", result["transactionId"] as Int)
                            }
                        )
                    )
                } else if (status == HttpStatusCode.BadRequest && result.containsKey("currentStatus")) {
                    call.respond(
                        status,
                        BaseResponse(
                            success = success,
                            message = message,
                            data = buildJsonObject {
                                put("appointmentId", result["appointmentId"] as Int)
                                put("currentStatus", result["currentStatus"] as String)
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
                println("✗ Lỗi trong quá trình thanh toán: ${e.message}")
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