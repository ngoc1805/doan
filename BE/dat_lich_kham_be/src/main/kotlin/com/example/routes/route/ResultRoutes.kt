package com.example.routes.route

import com.example.dao.ResultDAO
import com.example.dto.Request.ResultRequest
import com.example.dto.Response.BaseResponse
import com.example.dto.Response.ListResultResponse
import com.example.service.ResultService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.request.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement

fun Route.resultRoute(service: ResultService = ResultService()) {
    route("/api/benhnhan") {
        get("/results") {
            val userId = call.request.queryParameters["userId"]?.toIntOrNull()
            if (userId == null) {
                call.respondText("Missing or invalid userId", status = HttpStatusCode.BadRequest)
                return@get
            }
            val status = "Đã hoàn tất"
            val results = service.getResultsByUserIdAndStatus(userId, status)
            call.respond(ListResultResponse(results = results))
        }
    }
    route("/api/bacsi") {
        post("/results") {
            val req = call.receive<ResultRequest>()
            val resultDao: ResultDAO = service.createResult(req)
            val resultJson = Json.encodeToJsonElement(mapOf(
                "id" to resultDao.id.value,
                "appointmentId" to resultDao.appointmentId.value,
                "comment" to resultDao.comment,
                "dietRecommendation" to resultDao.dietRecommendation
            ))
            call.respond(
                BaseResponse(
                    success = true,
                    message = "Tạo kết quả thành công",
                    data = resultJson
                )
            )
        }
    }
}