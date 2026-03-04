package com.example.routes.route

import com.example.dto.Request.MealSkipRequest
import com.example.dto.Request.MealSubscriptionRequest
import com.example.dto.Response.BaseResponse
import com.example.service.MealService
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.put

fun Route.mealRoutes(mealService: MealService) {

    route("/api/benhnhan/meals") {

        // Đăng ký suất ăn lần đầu hoặc gia hạn
        post("/register") {
            val request = call.receive<MealSubscriptionRequest>()
            val result = mealService.registerMeal(request)

            if (result != null) {
                val (cycleId, totalCost) = result
                call.respond(
                    BaseResponse(
                        success = true,
                        message = "Đăng ký suất ăn thành công. Vui lòng thanh toán.",
                        data = Json.encodeToJsonElement(
                            buildJsonObject {
                                put("cycleId", cycleId)
                                put("totalCost", totalCost)
                                put("pricePerDay", 100000)
                                put("days", totalCost / 100000)
                            }
                        )
                    )
                )
            } else {
                call.respond(
                    HttpStatusCode.BadRequest, BaseResponse(
                        success = false,
                        message = "Đăng ký thất bại. Vui lòng kiểm tra trạng thái nhập viện.",
                        data = null
                    )
                )
            }
        }

        // Gia hạn suất ăn cho tuần tiếp theo
        post("/renew") {
            val inpatientId = call.request.queryParameters["inpatientId"]?.toIntOrNull()
            if (inpatientId == null) {
                call.respond(HttpStatusCode.BadRequest, "Thiếu inpatientId")
                return@post
            }

            // Kiểm tra có thể gia hạn không
            val canRenew = mealService.canRenew(inpatientId)
            if (!canRenew) {
                call.respond(
                    HttpStatusCode.BadRequest, BaseResponse(
                        success = false,
                        message = "Chưa thể gia hạn. Bạn chỉ có thể gia hạn từ thứ 6.",
                        data = null
                    )
                )
                return@post
            }

            val result = mealService.renewMeal(inpatientId)
            if (result != null) {
                val (cycleId, totalCost) = result
                call.respond(
                    BaseResponse(
                        success = true,
                        message = "Gia hạn thành công. Vui lòng thanh toán.",
                        data = Json.encodeToJsonElement(
                            buildJsonObject {
                                put("cycleId", cycleId)
                                put("totalCost", totalCost)
                                put("pricePerDay", 100000)
                                put("days", 5)
                            }
                        )
                    )
                )
            } else {
                call.respond(
                    HttpStatusCode.BadRequest, BaseResponse(
                        success = false,
                        message = "Gia hạn thất bại.",
                        data = null
                    )
                )
            }
        }

        // Cắt cơm hôm nay
        post("/skip") {
            val request = call.receive<MealSkipRequest>()
            val success = mealService.skipMeal(request)

            if (success) {
                call.respond(
                    BaseResponse(
                        success = true,
                        message = "Đã đánh dấu cắt cơm ngày ${request.skipDate}. Bạn sẽ nhận ngũ cốc và sữa.",
                        data = null
                    )
                )
            } else {
                call.respond(
                    HttpStatusCode.BadRequest, BaseResponse(
                        success = false,
                        message = "Không thể cắt cơm ngày này. Vui lòng kiểm tra lại.",
                        data = null
                    )
                )
            }
        }

        // Kiểm tra trạng thái ăn cơm hôm nay
        get("/status") {
            val inpatientId = call.request.queryParameters["inpatientId"]?.toIntOrNull()
            if (inpatientId == null) {
                call.respond(HttpStatusCode.BadRequest, "Thiếu inpatientId")
                return@get
            }

            val status = mealService.getMealStatusToday(inpatientId)
            call.respond(status)
        }

        // Lịch sử chu kỳ
        get("/history") {
            val inpatientId = call.request.queryParameters["inpatientId"]?.toIntOrNull()
            if (inpatientId == null) {
                call.respond(HttpStatusCode.BadRequest, "Thiếu inpatientId")
                return@get
            }

            val history = mealService.getMealHistory(inpatientId)
            call.respond(history)
        }

        // Thống kê
        get("/statistics") {
            val inpatientId = call.request.queryParameters["inpatientId"]?.toIntOrNull()
            if (inpatientId == null) {
                call.respond(HttpStatusCode.BadRequest, "Thiếu inpatientId")
                return@get
            }

            val stats = mealService.getMealStatistics(inpatientId)
            call.respond(stats)
        }

        // Kiểm tra có thể gia hạn không
        get("/can-renew") {
            val inpatientId = call.request.queryParameters["inpatientId"]?.toIntOrNull()
            if (inpatientId == null) {
                call.respond(HttpStatusCode.BadRequest, "Thiếu inpatientId")
                return@get
            }

            val canRenew = mealService.canRenew(inpatientId)
            call.respond(
                buildJsonObject {
                    put("canRenew", canRenew)
                }
            )
        }
    }

    // Route cho admin/bếp
    route("/api/admin/meals") {

        // Kích hoạt chu kỳ sau khi thanh toán (API này đã được xử lý trong payment route)
        post("/activate") {
            val cycleId = call.request.queryParameters["cycleId"]?.toIntOrNull()
            if (cycleId == null) {
                call.respond(HttpStatusCode.BadRequest, "Thiếu cycleId")
                return@post
            }
            val success = mealService.activateCycle(cycleId)
            if (success) {
                call.respond(BaseResponse(true, "Kích hoạt chu kỳ thành công", null))
            } else {
                call.respond(HttpStatusCode.NotFound, BaseResponse(false, "Không tìm thấy chu kỳ", null))
            }
        }

        // Đóng chu kỳ khi hết tuần
        post("/complete") {
            val cycleId = call.request.queryParameters["cycleId"]?.toIntOrNull()
            if (cycleId == null) {
                call.respond(HttpStatusCode.BadRequest, "Thiếu cycleId")
                return@post
            }
            val success = mealService.completeCycle(cycleId)
            if (success) {
                call.respond(BaseResponse(true, "Đóng chu kỳ thành công", null))
            } else {
                call.respond(HttpStatusCode.NotFound, BaseResponse(false, "Không tìm thấy chu kỳ", null))
            }
        }
    }
}