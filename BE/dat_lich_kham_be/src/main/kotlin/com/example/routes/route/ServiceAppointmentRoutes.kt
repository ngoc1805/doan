package com.example.routes.route

import com.example.dto.Request.ServiceAppointmentRequest
import com.example.dto.Request.UpdateServiceAppointmentStatusRequest
import com.example.dto.Response.BaseResponse
import com.example.dto.Response.ListServiceAppointment
import com.example.dto.Response.ListServiceItemResponse
import com.example.service.ServiceAppointmentService
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.LocalDate
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.encodeToJsonElement

fun Route.serviceAppointmentRoutes(service: ServiceAppointmentService) {
    route("/api/bacsi") {
        post("/service-appointments") {
            val request = call.receive<ServiceAppointmentRequest>()
            val result = service.createServiceAppointment(request)
            call.respond(
                BaseResponse(
                    success = true,
                    message = "Tạo lịch hẹn dịch vụ thành công",
                    data = Json.encodeToJsonElement(result)
                )
            )
        }
        get("/service-room-ids") {
            val appointmentId = call.request.queryParameters["appointmentId"]?.toIntOrNull()
            if (appointmentId == null) {
                call.respond(emptyList<Int>())
                return@get
            }
            val roomIds = service.getServiceRoomIdsByAppointmentId(appointmentId)
            call.respond(roomIds)
        }
    }
    route("/api/benhnhan"){
        get("/service-rooms") {
            val appointmentId = call.request.queryParameters["appointmentId"]?.toIntOrNull()
            if (appointmentId == null) {
                call.respond(ListServiceItemResponse(emptyList()))
                return@get
            }
            val rooms = service.getServiceRoomsByAppointmentId(appointmentId)
            call.respond(ListServiceItemResponse(rooms))
        }
    }
    route("/api/chucnang") {
        get("/service-appointments-by-room") {
            val serviceRoomId = call.request.queryParameters["serviceRoomId"]?.toIntOrNull()
            val status = call.request.queryParameters["status"]
            val appointmentStatus = call.request.queryParameters["appointmentStatus"]
            val examDateStr = call.request.queryParameters["examDate"]

            if (serviceRoomId == null) {
                call.respond(ListServiceAppointment(emptyList()))
                return@get
            }

            val examDate = examDateStr?.let {
                try { LocalDate.parse(it) } catch (e: Exception) { null }
            }

            val appointments = service.getServiceAppointmentsByRoomAndStatus(
                serviceRoomId, status, appointmentStatus, examDate
            )
            call.respond(ListServiceAppointment(appointments))
        }
        post("/service-appointments/update-status") {
            val req = call.receive<UpdateServiceAppointmentStatusRequest>()
            val success = service.updateServiceAppointmentStatus(req.serviceAppointmentId, req.status)
            if (success) {
                call.respond(
                    BaseResponse(
                        success = true,
                        message = "Cập nhật trạng thái lịch hẹn dịch vụ thành công",
                        data = null
                    )
                )
            } else {
                call.respond(
                    BaseResponse(
                        success = false,
                        message = "Không tìm thấy lịch hẹn dịch vụ",
                        data = null
                    )
                )
            }
        }
    }

}