package com.example.routes.route

import com.example.service.TransactionService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.http.*

fun Route.transactionRoutes(transactionService: TransactionService = TransactionService()) {
    route("/api/benhnhan") {
        get("/transactions/history/{userId}") {
            val userIdParam = call.parameters["userId"]
            val userId = userIdParam?.toIntOrNull()
            if (userId == null) {
                call.respond(HttpStatusCode.BadRequest, "userId không hợp lệ hoặc bị thiếu")
                return@get
            }

            val history = transactionService.getTransactionHistory(userId)
            call.respond(history)
        }
    }
}