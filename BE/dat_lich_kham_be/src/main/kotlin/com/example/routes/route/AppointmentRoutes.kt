package com.example.controller

import com.example.dto.Request.AppointmentRequest
import com.example.dto.Request.FreeTimeRequest
import com.example.dto.Response.*
import com.example.service.AppointmentService
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import java.util.concurrent.ConcurrentHashMap

val appointmentSessions = ConcurrentHashMap<String, DefaultWebSocketServerSession>()
fun Route.appointmentRoutes(appointmentService: AppointmentService) {
    route("api/benhnhan") {
        post("/free-time") {
            try {
                val req = call.receive<FreeTimeRequest>()
                val freeSlots = appointmentService.getFreeTimeSlots(req)
                call.respond(FreeTimeResponse(freeSlots))
            } catch (e: Exception) {
                call.respond(
                    FreeTimeResponse(emptyList())
                )
            }
        }
        //
        post("/appointment") {
            try {
                val req = call.receive<AppointmentRequest>()
                val appointment = appointmentService.createAppointment(req)
                call.respond(BaseResponse(true, "Đặt lịch thành công", Json.encodeToJsonElement(appointment)))
            } catch (e: Exception) {
                call.respond(BaseResponse(false, "Đặt lịch thất bại: ${e.message}", null))
            }
        }
        //
        get("/appointments") {
            val userId = call.request.queryParameters["userId"]?.toIntOrNull()
            val statusList = call.request.queryParameters.getAll("status") // List<String>?

            if (userId == null) {
                call.respond(AppointmentByUserIdListResponse(emptyList()))
                return@get
            }

            val items = appointmentService.getAppointmentsByUserIdAndStatus(userId, statusList)
            call.respond(AppointmentByUserIdListResponse(items))
        }
        //
        post("/update-appointment-status") {
            val req = call.receive<com.example.dto.Request.UpdateAppointmentSatatusRequest>()
            val success = appointmentService.updateAppointmentStatus(req.appointmentId, req.status)
            if (success) {
                call.respond(
                    BaseResponse(
                        success = true,
                        message = "Cập nhật trạng thái lịch khám thành công"
                    )
                )
            } else {
                call.respond(
                    BaseResponse(
                        success = false,
                        message = "Không tìm thấy lịch khám"
                    )
                )
            }
        }
        //
        get("/nearest-appointment") {
            val userId = call.request.queryParameters["userId"]?.toIntOrNull()
            if (userId == null) {
                call.respondText("Missing or invalid userId", status = io.ktor.http.HttpStatusCode.BadRequest)
                return@get
            }
            val result: AppointmentItem? = appointmentService.getNearestUpcomingAppointment(userId)
            if (result != null) {
                call.respond(result)
            } else {
                call.respondText("Không có lịch khám nào sắp tới", status = io.ktor.http.HttpStatusCode.NotFound)
            }
        }
    }
    webSocket("api/benhnhan/wsfree-time") {
        val clientId = hashCode().toString()
        appointmentSessions[clientId] = this
        try {
            incoming.consumeEach { frame ->
                if (frame is Frame.Text) {
                    // Nếu muốn nhận message từ client, xử lý tại đây
                }
            }
        } finally {
            appointmentSessions.remove(clientId)
        }
    }
    route("api/bacsi"){
        get("/appointments-by-doctor") {
            val doctorId = call.request.queryParameters["doctorId"]?.toIntOrNull()
            val examDateStr = call.request.queryParameters["examDate"]
            val statusList = call.request.queryParameters.getAll("status")

            if (doctorId == null) {
                call.respond(AppointmentByDoctorIdListResponse(emptyList()))
                return@get
            }

            val examDate = examDateStr?.let {
                try { kotlinx.datetime.LocalDate.parse(it) } catch (e: Exception) { null }
            }

            val appointments = appointmentService.getAppointmentsByDoctorIdAndDateAndStatus(
                doctorId, examDate, statusList
            )
            call.respond(AppointmentByDoctorIdListResponse(appointments))
        }
        post("/update-appointment-status") {
            val req = call.receive<com.example.dto.Request.UpdateAppointmentSatatusRequest>()
            val success = appointmentService.updateAppointmentStatus(req.appointmentId, req.status)
            if (success) {
                call.respond(
                    BaseResponse(
                        success = true,
                        message = "Cập nhật trạng thái lịch khám thành công"
                    )
                )
            } else {
                call.respond(
                    BaseResponse(
                        success = false,
                        message = "Không tìm thấy lịch khám"
                    )
                )
            }
        }
    }
}