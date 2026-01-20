package com.example.routes.route

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun Route.adminRoutes() {

        route("/api/benhnhan") {
            get("/hh") {
                val principal = call.authentication.principal<JWTPrincipal>()
                println("Principal in handler: $principal")
                call.respondText("Hello từ admin API!")
            }
        }
}