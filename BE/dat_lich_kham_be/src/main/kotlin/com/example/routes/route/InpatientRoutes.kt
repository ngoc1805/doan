package com.example.routes.route

import com.example.dto.Request.InpatientRequest
import com.example.dto.Request.UpdateAddressRequest
import com.example.dto.Request.UpdateStatusInpatient
import com.example.dto.Response.BaseResponse
import com.example.dto.Response.InpatientListResponse
import com.example.service.InpatientService
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import kotlinx.serialization.json.JsonNull

fun Route.inpatientRoutes(inpatientService: InpatientService){
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
    }
    route("/api/bacsi"){
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
            val newInpatientId = inpatientService.createInpatient(request.userId)
            call.respond(
                BaseResponse(
                    success = true,
                    message = "Tạo mới thành công.",
                    data = null // hoặc trả về id nếu muốn
                )
            )
        }
    }
    route("/api/admin"){
        get("/inpatients/by-status") {
            val status = call.request.queryParameters["status"]
            if (status == null) {
                call.respond(InpatientListResponse(emptyList()))
                return@get
            }
            val response = inpatientService.getInpatientsByStatus(status)
            call.respond(response)
        }
            //
        post("/inpatients/discharge") {
            val req = call.receive<UpdateStatusInpatient>()
            val success = inpatientService.dischargeInpatientById(req.id)
            if (success) {
                call.respond(BaseResponse(true, "Đã cập nhật trạng thái xuất viện thành công", null))
            } else {
                call.respond(BaseResponse(false, "Không tìm thấy bản ghi nhập viện với id này", null))
            }
        }
        //
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