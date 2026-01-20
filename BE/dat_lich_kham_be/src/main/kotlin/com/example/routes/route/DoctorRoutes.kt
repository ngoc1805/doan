package com.example.routes.route

import com.example.service.DoctorService
import com.example.dto.Response.DoctorListResponse
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.doctorRoutes(doctorService: DoctorService) {
    route("/api/benhnhan") {
        get("/doctor") {
            val departmentId = call.request.queryParameters["departmentId"]?.toIntOrNull()
            val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
            val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 20

            if (departmentId == null) {
                call.respondText("Missing or invalid departmentId", status = io.ktor.http.HttpStatusCode.BadRequest)
                return@get
            }
            val doctors = doctorService.getDoctorsByDepartmentPaged(departmentId, page, size)
            call.respond(DoctorListResponse(doctors))
        }
    }
    route("/api/bacsi") {
        get("/doctor") {
            val accountId = call.request.queryParameters["accountId"]?.toIntOrNull()
            if (accountId == null) {
                call.respondText("Missing or invalid accountId", status = io.ktor.http.HttpStatusCode.BadRequest)
                return@get
            }
            val info = doctorService.getDoctorByAccountId(accountId)
            if (info != null) {
                call.respond(info)
            } else {
                call.respondText("Doctor not found", status = io.ktor.http.HttpStatusCode.NotFound)
            }
        }
    }
    route("api/admin"){
        get("/doctors") {
            val response = doctorService.getAllDoctors()
            call.respond(response)
        }
    }
}