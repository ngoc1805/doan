package com.example.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*

val RoleAuthorizationPlugin = createApplicationPlugin(name = "RoleAuthorization") {
    onCall { call ->

        println("[PLUGIN] RoleAuthorizationPlugin triggered for path: ${call.request.path()}")
        val path = call.request.path()
        println("=== AUTHORIZATION CHECK ===")
        println("Request path: $path")

        // Chỉ kiểm tra role cho các route đã được authenticate
        // Bỏ qua nếu không phải là protected route
        if (!path.startsWith("/api/admin") &&
            !path.startsWith("/api/bacsi") &&
            !path.startsWith("/api/benhnhan") &&
            !path.startsWith("/api/chucnang")) {
            println("Not a protected route - skipping authorization")
            return@onCall
        }

        // Lấy principal từ JWT (chỉ có sau khi JWT auth đã chạy)
        val principal = call.authentication.principal<JWTPrincipal>()
        println("JWT Principal: ${if (principal != null) "Found" else "Not found"}")

        // Nếu không có principal trong protected route -> có lỗi
        if (principal == null) {
            println("No principal in protected route - this should not happen if auth is properly configured")
            return@onCall
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
            else -> true
        }

        if (!isAuthorized) {
            println("Access denied - returning 403")
            call.respondText("Không đủ quyền truy cập", status = HttpStatusCode.Forbidden)
            return@onCall
        }

        println("Access granted")
    }
}