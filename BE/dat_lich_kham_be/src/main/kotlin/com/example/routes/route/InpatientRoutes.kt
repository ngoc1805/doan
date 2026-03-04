package com.example.routes.route

import com.example.dto.Request.InpatientRequest
import com.example.dto.Request.UpdateAddressRequest
import com.example.dto.Request.UpdateStatusInpatient
import com.example.dto.Response.BaseResponse
import com.example.dto.Response.InpatientListResponse
import com.example.service.InpatientService
import com.example.service.MealService
import com.example.repository.UserRepository
import com.example.repository.TransactionRepository
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

fun Route.inpatientRoutes(
    inpatientService: InpatientService,
    mealService: MealService? = null,
    userRepository: UserRepository? = null,
    transactionRepository: TransactionRepository? = null
) {
    route("/api/benhnhan") {
        get("/inpatients/check-admitted") {
            val userId = call.request.queryParameters["userId"]?.toIntOrNull()
            if (userId == null) {
                call.respond(HttpStatusCode.BadRequest, false)
                return@get
            }
            val exists = inpatientService.hasInpatientWithStatus(userId, "Đã nhập viện")
            call.respond(exists)
        }

        get("/inpatients/address") {
            val userId = call.request.queryParameters["userId"]?.toIntOrNull()
            if (userId == null) {
                call.respond(HttpStatusCode.BadRequest, "Thiếu hoặc sai userId")
                return@get
            }
            val address = inpatientService.getInpatientAddress(userId, "Đã nhập viện")
            if (address != null) {
                call.respond(address)
            } else {
                call.respond(HttpStatusCode.NotFound, "Không tìm thấy địa chỉ nhập viện cho userId này")
            }
        }

        get("/inpatients/current") {
            val userId = call.request.queryParameters["userId"]?.toIntOrNull()
            if (userId == null) {
                call.respond(HttpStatusCode.BadRequest, "Thiếu hoặc sai userId")
                return@get
            }
            val inpatient = inpatientService.getCurrentInpatient(userId)
            if (inpatient != null) {
                call.respond(inpatient)
            } else {
                call.respond(HttpStatusCode.NotFound, "Không tìm thấy bản ghi nội trú hiện tại")
            }
        }

        // Lấy lịch sử nội trú (đã xuất viện)
        get("/inpatients/history") {
            val userId = call.request.queryParameters["userId"]?.toIntOrNull()
            if (userId == null) {
                call.respond(HttpStatusCode.BadRequest, "Thiếu hoặc sai userId")
                return@get
            }
            val history = inpatientService.getInpatientHistory(userId)
            call.respond(history)
        }
    }

    route("/api/bacsi") {
        post("/inpatients") {
            val request = call.receive<InpatientRequest>()
            // Kiểm tra đã nhập viện chưa
            val hasAdmittedOrWaiting = inpatientService.hasInpatientWithStatus(request.userId, "Đã nhập viện") ||
                    inpatientService.hasInpatientWithStatus(request.userId, "Đang chờ")
            if (hasAdmittedOrWaiting) {
                call.respond(
                    BaseResponse(
                        success = false,
                        message = "Bệnh nhân đã nhập viện, không thể tạo mới.",
                        data = JsonNull
                    )
                )
                return@post
            }
            // Tạo mới
            val newInpatientId = inpatientService.createInpatient(request.userId, request.appointmentId)
            call.respond(
                BaseResponse(
                    success = true,
                    message = "Tạo mới thành công.",
                    data = buildJsonObject {
                        put("inpatientId", newInpatientId)
                    }
                )
            )
        }
    }

    route("/api/admin") {
        get("/inpatients/by-status") {
            val status = call.request.queryParameters["status"]
            if (status == null) {
                call.respond(InpatientListResponse(emptyList()))
                return@get
            }
            val response = inpatientService.getInpatientsByStatus(status)
            call.respond(response)
        }

        // Xuất viện - Có logic hoàn tiền suất ăn
        post("/inpatients/discharge") {
            val req = call.receive<UpdateStatusInpatient>()

            // Lấy thông tin inpatient để biết userId
            val inpatient = inpatientService.getInpatientById(req.id)
            if (inpatient == null) {
                call.respond(
                    HttpStatusCode.NotFound,
                    BaseResponse(false, "Không tìm thấy bản ghi nhập viện với id này", null)
                )
                return@post
            }

            // Tính toán hoàn tiền nếu có chu kỳ suất ăn đang active
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
            val refundAmount = if (mealService != null) {
                mealService.calculateRefund(req.id, today)
            } else {
                0
            }

            // Xuất viện
            val success = inpatientService.dischargeInpatientById(req.id)

            if (success) {
                // Nếu có tiền hoàn lại, cập nhật số dư và tạo transaction
                if (refundAmount > 0 && userRepository != null && transactionRepository != null) {
                    org.jetbrains.exposed.sql.transactions.transaction {
                        // Lấy số dư hiện tại
                        val currentBalance = userRepository.getBalanceByUserId(inpatient.userId)
                        if (currentBalance != null) {
                            // Cộng tiền hoàn lại
                            val newBalance = currentBalance + refundAmount
                            userRepository.updateBalanceByUserId(inpatient.userId, newBalance)

                            // Tạo transaction
                            transactionRepository.addTransaction(
                                userId = inpatient.userId,
                                category = "Hoàn tiền",
                                transactionType = "Hoàn tiền suất ăn khi xuất viện",
                                amount = refundAmount.toLong(),
                                isIncome = true
                            )
                        }
                    }
                }

                call.respond(
                    BaseResponse(
                        success = true,
                        message = if (refundAmount > 0) {
                            "Đã cập nhật trạng thái xuất viện thành công. Hoàn lại ${refundAmount} VNĐ."
                        } else {
                            "Đã cập nhật trạng thái xuất viện thành công."
                        },
                        data = buildJsonObject {
                            put("refundAmount", refundAmount)
                        }
                    )
                )
            } else {
                call.respond(
                    HttpStatusCode.NotFound,
                    BaseResponse(false, "Không tìm thấy bản ghi nhập viện với id này", null)
                )
            }
        }

        // Cập nhật địa chỉ và nhập viện
        post("/inpatients/update-address") {
            val req = call.receive<UpdateAddressRequest>()
            val success = inpatientService.updateAddressAndAdmit(req.id, req.address)
            if (success) {
                call.respond(
                    BaseResponse(
                        success = true,
                        message = "Cập nhật địa chỉ và trạng thái nhập viện thành công.",
                        data = JsonNull
                    )
                )
            } else {
                call.respond(
                    BaseResponse(
                        success = false,
                        message = "Không tìm thấy bản ghi nhập viện với id này.",
                        data = JsonNull
                    )
                )
            }
        }
    }
}