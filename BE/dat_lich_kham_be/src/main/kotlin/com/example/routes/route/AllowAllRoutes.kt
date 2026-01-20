package com.example.routes.route

import MenuListResponse
import com.example.dao.AccountDAO
import com.example.dao.UsersDAO
import com.example.dto.Request.NotificationRequest
import com.example.dto.Response.BaseResponse
import com.example.dto.Response.CanteenResponse
import com.example.dto.Response.ListServiceItemResponse
import com.example.models.Notification
import com.example.service.MenuService
import com.example.service.NotificationService
import com.example.service.ServiceRoomService
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.allowallRoutets(
    serviceromm: ServiceRoomService = ServiceRoomService(),
    servicenoti: NotificationService = NotificationService(),
    menuService: MenuService = MenuService()
) {
    route("api/notifications") {
        post {
            val req = call.receive<NotificationRequest>()
            val notification = servicenoti.createNotification(req)
            call.respond(
                BaseResponse(
                    success = true,
                    message = "Notification created successfully",
                    data = Json.encodeToJsonElement(
                        Notification.serializer(),
                        notification
                    )
                )
            )
        }
    }
    route("/api") {
        get("/service-rooms") {
            val rooms = serviceromm.getAllRooms()
            call.respond(ListServiceItemResponse(servicerooms = rooms))
        }
    }
    route("/api"){
        static("/uploads") {
            files("uploads")
        }
    }
    route("/api"){
        get("/menus") {
            val isDisplayParam = call.request.queryParameters["isDisplay"]
            val menus = isDisplayParam?.toBooleanStrictOrNull()?.let {
                menuService.getMenusByIsDisplay(it)
            } ?: menuService.getAllMenus()
            call.respond(MenuListResponse(menus))
        }
    }
    route("/api"){
        get("/canteen/info") {
            val accountId = 15
            val canteenResponse = transaction {
                val user = UsersDAO.find { com.example.Tables.Users.accountId eq accountId }.firstOrNull()
                val fmcToken = AccountDAO.findById(accountId)?.fmctoken ?: ""
                if (user != null) {
                    CanteenResponse(
                        userId = user.id.value,
                        fmcToken = fmcToken
                    )
                } else null
            }
            if (canteenResponse != null) {
                call.respond(canteenResponse)
            } else {
                call.respondText("Không tìm thấy user/account với id 15", status = io.ktor.http.HttpStatusCode.NotFound)
            }
        }
    }


}