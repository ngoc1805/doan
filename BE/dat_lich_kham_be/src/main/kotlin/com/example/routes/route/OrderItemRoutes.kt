package com.example.routes

import com.example.dto.Request.OrderItemRequest
import com.example.dto.Response.BaseResponse
import com.example.service.OrderItemService
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement

fun Route.orderItemRoutes(orderItemService: OrderItemService) {
    route("/api/benhnhan") {
        post("/order-items") {
            val request = call.receive<OrderItemRequest>()
            val orderItem = orderItemService.createOrderItem(
                orderId = request.orderId,
                menuId = request.menuId,
                quantity = request.quantity
            )
            call.respond(
                BaseResponse(
                    success = true,
                    message = "Thêm món vào đơn thành công",
                    data = Json.encodeToJsonElement(orderItem)
                )
            )
        }
    }
}