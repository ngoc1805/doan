package com.example.routes.route

import com.example.dto.Request.CreateDepartmentRequest
import com.example.dto.Response.BaseResponse
import com.example.service.DepartmentService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.JsonNull

fun Route.departmentRoutes(departmentService: DepartmentService) {
    route("/api") {
        get("/departments") {
            val departments = departmentService.getAllDepartments()
            call.respond(departments)
        }

        post("/departments") {
            val request = call.receive<CreateDepartmentRequest>()
            val isCreated = departmentService.createDepartment(request.name, request.description)
            if (isCreated) {
                call.respond(HttpStatusCode.Created, BaseResponse(true, "Tạo khoa thành công", JsonNull))
            } else {
                call.respond(HttpStatusCode.BadRequest, BaseResponse(false, "Khoa đã tồn tại", JsonNull))
            }
        }
    }
}