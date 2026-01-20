package com.example.routes.route

import com.example.service.ServiceRoomService
import com.example.dto.Response.ListServiceItemResponse
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.serviceRoomRoutes(service: ServiceRoomService = ServiceRoomService()) {
    route("api/chucnang"){
            get("/service-room") {
                val accountId = call.request.queryParameters["accountId"]?.toIntOrNull()
                if (accountId == null) {
                    call.respondText("Invalid accountId", status = io.ktor.http.HttpStatusCode.BadRequest)
                    return@get
                }
                val room = service.getRoomByAccountId(accountId)
                if (room != null) {
                    call.respond(room)
                } else {
                    call.respondText("Not found", status = io.ktor.http.HttpStatusCode.NotFound)
                }
            }
        }

}