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
import kotlinx.serialization.json.encodeToJsonElement

fun Route.mealRoutes(mealService: MealService) {

    route("/api/benhnhan/meals") {

        // Đăng ký suất ăn lần đầu
        post("/register") {
            val request = call.receive<MealSubscriptionRequest>()
            val cycleId = mealService.registerMeal(request)

            if (cycleId != null) {
                call.respond(
                    BaseResponse(
                        success = true,
                        message = "Đăng ký suất ăn thành công. Vui lòng thanh toán.",
                        data = Json.encodeToJsonElement(mapOf("cycleId" to cycleId))
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

        // Gia hạn suất ăn
        post("/renew") {
            val inpatientId = call.request.queryParameters["inpatientId"]?.toIntOrNull()
            if (inpatientId == null) {
                call.respond(HttpStatusCode.BadRequest, "Thiếu inpatientId")
                return@post
            }

            val cycleId = mealService.renewMeal(inpatientId)
            if (cycleId != null) {
                call.respond(
                    BaseResponse(
                        success = true,
                        message = "Gia hạn thành công. Vui lòng thanh toán.",
                        data = Json.encodeToJsonElement(mapOf("cycleId" to cycleId))
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
                        message = "Đã đánh dấu cắt cơm ngày ${request.skipDate}",
                        data = null
                    )
                )
            } else {
                call.respond(
                    HttpStatusCode.BadRequest, BaseResponse(
                        success = false,
                        message = "Không thể cắt cơm ngày này.",
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
    }

    // Route cho admin/bếp
    route("/api/admin/meals") {

        // Kích hoạt chu kỳ sau khi thanh toán
        post("/activate") {
            val cycleId = call.request.queryParameters["cycleId"]?.toIntOrNull()
                call.request.queryParameters["cycleId"]?.toIntOrNull()
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
    }
}