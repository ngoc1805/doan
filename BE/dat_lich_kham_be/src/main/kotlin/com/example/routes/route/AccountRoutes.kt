package com.example.routes.route

import com.example.dto.Request.ChangePasswordRequest
import com.example.dto.Request.CreateDoctorRequest
import com.example.dto.Request.CreateServiceRoomRequest
import com.example.dto.Request.UpdateFmcTokenRequest
import com.example.dto.Response.BaseResponse
import com.example.service.AccountService
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.accountRoutes(accountService: AccountService) {
    route("/api") {
        post("/update-fmc-token") {
            val req = call.receive<UpdateFmcTokenRequest>()
            val updated = accountService.updateFmcToken(req.accountId, req.fcmToken)
            if (updated) {
                call.respond(BaseResponse(true, "Cập nhật FMC token thành công"))
            } else {
                call.respond(BaseResponse(false, "Không tìm thấy tài khoản"))
            }
        }
        post("/doctor/create") {
            val req = call.receive<CreateDoctorRequest>()
            val success = accountService.createDoctor(
                name = req.name,
                code = req.code,
                departmentId = req.departmentId,
                examPrice = req.examPrice
            )
            if (success) {
                call.respond(BaseResponse(true, "Tạo bác sĩ thành công"))
            } else {
                call.respond(BaseResponse(false, "Mã bác sĩ đã tồn tại!"))
            }
        }
        post("/service-room/create") {
            val req = call.receive<CreateServiceRoomRequest>()
            val success = accountService.createServiceRoom(
                name = req.name,
                code = req.code,
                address = req.address,
                examPrice = req.examPrice
            )
            if (success) {
                call.respond(BaseResponse(true, "Tạo phòng dịch vụ thành công"))
            } else {
                call.respond(BaseResponse(false, "Mã phòng dịch vụ đã tồn tại!"))
            }
        }
        post("/change-password") {
            val req = call.receive<ChangePasswordRequest>()
            val success = accountService.changePassword(req.accountId, req.oldPassword, req.newPassword)
            if (success) {
                call.respond(BaseResponse(true, "Đổi mật khẩu thành công"))
            } else {
                call.respond(BaseResponse(false, "Mật khẩu cũ không đúng hoặc tài khoản không tồn tại"))
            }
        }
    }
}