package com.example.controller

import com.example.dto.Request.PinRequest
import com.example.dto.Request.UpdateBalanceRequest
import com.example.dto.Request.UserRequest
import com.example.dto.Response.BaseResponse
import com.example.service.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement

fun Route.userRoutes(userService: UserService) {
    route("api/benhnhan") {
        //
        put("/update") {
            try {
                val req = call.receive<UserRequest>()
                val success = userService.updateUserInfo(req)
                if (success) {
                    call.respond(BaseResponse(true, "Cập nhật thông tin thành công"))
                } else {
                    call.respond(BaseResponse(false, "Cập nhật thất bại hoặc không tìm thấy tài khoản"))
                }
            } catch (e: Exception) {
                println("Lỗi nhận dữ liệu update: ${e.message}")
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = BaseResponse(false, "Dữ liệu gửi lên không hợp lệ: ${e.message}")
                )
            }
        }
        //
        get("/check-info") {
            val accountId = call.parameters["accountId"]?.toIntOrNull()
            if (accountId == null) {
                call.respond(BaseResponse(false, "Thiếu hoặc sai accountId"))
                return@get
            }
            val isComplete = userService.isUserInfoComplete(accountId)
            if (isComplete) {
                call.respond(BaseResponse(true, "Thông tin cá nhân đã đầy đủ"))
            } else {
                call.respond(BaseResponse(false, "Thông tin cá nhân chưa đầy đủ"))
            }
        }
        //
        get("/info") {
            val accountId = call.parameters["accountId"]?.toIntOrNull()
            if (accountId == null) {
                call.respond(BaseResponse(false, "Thiếu hoặc sai accountId"))
                return@get
            }
            val user = userService.getUserByAccountId(accountId)
            if (user != null) {
                call.respond(user)
            } else {
                call.respond(BaseResponse(false, "Không tìm thấy tài khoản"))
            }
        }
        //
        put("/update-balance") {
            try {
                val req = call.receive<UpdateBalanceRequest>()
                val success = userService.updateBalanceByUserId(req.userId, req.balance)
                if (success) {
                    call.respond(BaseResponse(true, "Cập nhật số dư thành công"))
                } else {
                    call.respond(BaseResponse(false, "Không tìm thấy người dùng"))
                }
            } catch (e: Exception) {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = BaseResponse(false, "Lỗi dữ liệu hoặc server: ${e.message}")
                )
            }
        }
        //
        get("/has-pin") {
            val userId = call.parameters["userId"]?.toIntOrNull()
            if (userId == null) {
                call.respond(BaseResponse(false, "Thiếu hoặc sai userId"))
                return@get
            }
            val hasPin = userService.hasPin(userId)
            call.respond(BaseResponse(true, "Kiểm tra PIN", Json.encodeToJsonElement(hasPin)))
        }
        //
        put("/update-pin") {
            val req = call.receive<PinRequest>()
            val success = userService.updatePin(req.userId, req.pinCode)
            call.respond(BaseResponse(success, if (success) "Cập nhật PIN thành công" else "Không tìm thấy người dùng"))
        }
        //
        post("/compare-pin") {
            val req = call.receive<PinRequest>()
            val matched = userService.comparePin(req.userId, req.pinCode)
            call.respond(BaseResponse(true, if (matched) "PIN đúng" else "PIN sai", Json.encodeToJsonElement(matched)))
        }
    }
    route("/api/nhaan"){
        get("/info") {
            val accountId = call.parameters["accountId"]?.toIntOrNull()
            if (accountId == null) {
                call.respond(BaseResponse(false, "Thiếu hoặc sai accountId"))
                return@get
            }
            val user = userService.getUserByAccountId(accountId)
            if (user != null) {
                call.respond(user)
            } else {
                call.respond(BaseResponse(false, "Không tìm thấy tài khoản"))
            }
        }
    }
}