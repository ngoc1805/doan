package com.example.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

// Extension function để thêm authorization cho route
fun Route.withRoleAuthorization(build: Route.() -> Unit) {
    intercept(ApplicationCallPipeline.Call) {
        println("[INTERCEPT] Authorization check for path: ${call.request.path()}")
        val path = call.request.path()

        // Lấy principal (JWT auth đã chạy xong tại đây)
        val principal = call.authentication.principal<JWTPrincipal>()
        println("JWT Principal: ${if (principal != null) "Found" else "Not found"}")

        if (principal == null) {
            println("No principal found - authentication failed")
            call.respondText("Authentication required", status = HttpStatusCode.Unauthorized)
            finish()
            return@intercept
        }

        val roleName = principal.payload.getClaim("role").asString()
        println("User role: $roleName")

        // Quy tắc phân quyền theo path
        val isAuthorized = when {
            path.startsWith("/api/admin") -> {
                val authorized = roleName == "admin"
                println("Admin endpoint - authorized: $authorized")
                authorized
            }
            path.startsWith("/api/bacsi") -> {
                val authorized = roleName == "bacsi" || roleName == "admin"
                println("Bacsi endpoint - authorized: $authorized")
                authorized
            }
            path.startsWith("/api/benhnhan") -> {
                val authorized = roleName == "benhnhan" || roleName == "admin"
                println("Benhnhan endpoint - authorized: $authorized")
                authorized
            }
            path.startsWith("/api/chucnang") -> {
                val authorized = roleName == "chucnang" || roleName == "admin"
                println("Phongchucnang endpoint - authorized: $authorized")
                authorized
            }
            path.startsWith("/api/nhaan") -> {
                val authorized = roleName == "nhaan" || roleName == "admin"
                println("Nhaanbenhhvien endpoint - authorized: $authorized")
                authorized
            }
            else -> {
                println("Unknown protected route - denying access")
                false
            }
        }

        if (!isAuthorized) {
            println("Access denied - returning 403")
            call.respondText("Không đủ quyền truy cập", status = HttpStatusCode.Forbidden)
            finish()
            return@intercept
        }

        println("Access granted - proceeding")
        proceed()
    }
    build()
}

// Alternative: Route-scoped plugin approach
val RoleAuthorizationRoutePlugin = createRouteScopedPlugin(name = "RoleAuthorization") {
    onCallReceive { call ->
        println("[PLUGIN] Route-level authorization check for path: ${call.request.path()}")
        val path = call.request.path()

        val principal = call.authentication.principal<JWTPrincipal>()
        println("JWT Principal: ${if (principal != null) "Found" else "Not found"}")

        if (principal == null) {
            call.respondText("Authentication required", status = HttpStatusCode.Unauthorized)
            return@onCallReceive
        }

        val roleName = principal.payload.getClaim("role").asString()
        val isAuthorized = when {
            path.startsWith("/api/admin") -> roleName == "admin"
            path.startsWith("/api/bacsi") -> roleName == "bacsi" || roleName == "admin"
            path.startsWith("/api/benhnhan") -> roleName == "benhnhan" || roleName == "admin"
            path.startsWith("/api/chucnang") -> roleName == "chucnang" || roleName == "admin"
            path.startsWith("/api/nhaan") -> roleName == "nhaan" || roleName == "admin"
            else -> false
        }

        if (!isAuthorized) {
            call.respondText("Không đủ quyền truy cập", status = HttpStatusCode.Forbidden)
        }
    }
}