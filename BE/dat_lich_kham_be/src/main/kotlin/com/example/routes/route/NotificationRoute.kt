package com.example.routes.route

import com.example.dto.Request.NotificationRequest
import com.example.dto.Response.BaseResponse
import com.example.dto.Response.NotificationItem
import com.example.dto.Response.NotificationListResponse
import com.example.service.NotificationService
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.serialization.builtins.serializer

fun Route.notificationRoute(service: NotificationService = NotificationService()) {
    authenticate("auth-jwt"){
        route("api/benhnhan") {
            get("/list-notification") {
                val userId = call.request.queryParameters["userId"]?.toIntOrNull()
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val pageSize = call.request.queryParameters["pageSize"]?.toIntOrNull() ?: 30

                if (userId == null) {
                    call.respondText("Missing or invalid userId", status = io.ktor.http.HttpStatusCode.BadRequest)
                    return@get
                }
                val notifications = service.getNotificationsByUserIdPaged(userId, page, pageSize)
                val notificationItems = notifications.map {
                    NotificationItem(
                        id = it.id,
                        content = it.content,
                        isSeen = it.isSeen,
                        createdAt = it.createdAt,
                        path = it.path
                    )
                }
                call.respond(NotificationListResponse(notificationItems))
            }
            //
            get("/all-received") {
                val userId = call.request.queryParameters["userId"]?.toIntOrNull()
                if (userId == null) {
                    call.respond(BaseResponse(false, "Invalid userId"))
                    return@get
                }
                val result = service.areAllNotificationsReceived(userId)
                call.respond(BaseResponse(true, "All notifications received: $result", kotlinx.serialization.json.Json.encodeToJsonElement(Boolean.serializer(), result)))
            }

            //
            post("/mark-all-received") {
                val userId = call.request.queryParameters["userId"]?.toIntOrNull()
                if (userId == null) {
                    call.respond(BaseResponse(false, "Invalid userId"))
                    return@post
                }
                service.setAllNotificationsReceived(userId)
                call.respond(BaseResponse(true, "All notifications marked as received"))
            }
            //
            post("/mark-seen") {
                val notificationId = call.request.queryParameters["id"]?.toIntOrNull()
                if (notificationId == null) {
                    call.respond(
                        BaseResponse(
                            success = false,
                            message = "Missing or invalid notification id",
                            data = null
                        )
                    )
                    return@post
                }
                val updated = service.markNotificationSeen(notificationId)
                if (updated) {
                    call.respond(
                        BaseResponse(
                            success = true,
                            message = "Notification marked as seen",
                            data = null
                        )
                    )
                } else {
                    call.respond(
                        BaseResponse(
                            success = false,
                            message = "Notification not found",
                            data = null
                        )
                    )
                }
            }
            //
        }

        route("api/nhaan") {
            get("/list-notification") {
                val userId = call.request.queryParameters["userId"]?.toIntOrNull()
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val pageSize = call.request.queryParameters["pageSize"]?.toIntOrNull() ?: 30

                if (userId == null) {
                    call.respondText("Missing or invalid userId", status = io.ktor.http.HttpStatusCode.BadRequest)
                    return@get
                }
                val notifications = service.getNotificationsByUserIdPaged(userId, page, pageSize)
                val notificationItems = notifications.map {
                    NotificationItem(
                        id = it.id,
                        content = it.content,
                        isSeen = it.isSeen,
                        createdAt = it.createdAt,
                        path = it.path
                    )
                }
                call.respond(NotificationListResponse(notificationItems))
            }
            //
            get("/all-received") {
                val userId = call.request.queryParameters["userId"]?.toIntOrNull()
                if (userId == null) {
                    call.respond(BaseResponse(false, "Invalid userId"))
                    return@get
                }
                val result = service.areAllNotificationsReceived(userId)
                call.respond(BaseResponse(true, "All notifications received: $result", kotlinx.serialization.json.Json.encodeToJsonElement(Boolean.serializer(), result)))
            }

            //
            post("/mark-all-received") {
                val userId = call.request.queryParameters["userId"]?.toIntOrNull()
                if (userId == null) {
                    call.respond(BaseResponse(false, "Invalid userId"))
                    return@post
                }
                service.setAllNotificationsReceived(userId)
                call.respond(BaseResponse(true, "All notifications marked as received"))
            }
            //
            post("/mark-seen") {
                val notificationId = call.request.queryParameters["id"]?.toIntOrNull()
                if (notificationId == null) {
                    call.respond(
                        BaseResponse(
                            success = false,
                            message = "Missing or invalid notification id",
                            data = null
                        )
                    )
                    return@post
                }
                val updated = service.markNotificationSeen(notificationId)
                if (updated) {
                    call.respond(
                        BaseResponse(
                            success = true,
                            message = "Notification marked as seen",
                            data = null
                        )
                    )
                } else {
                    call.respond(
                        BaseResponse(
                            success = false,
                            message = "Notification not found",
                            data = null
                        )
                    )
                }
            }
            //
        }
    }

}