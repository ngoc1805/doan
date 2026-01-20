// File: com/example/websocket/WebSocketRoutes.kt
package com.example.websocket

import com.example.config.JWTConfig
import com.example.websocket.handlers.notificationWebSocket
import io.ktor.server.routing.*

/**
 * Tập trung tất cả WebSocket routes ở đây
 */
fun Route.configureWebSocketRoutes(jwtConfig: JWTConfig? = null) {
    // WebSocket cho notification - truyền jwtConfig
    notificationWebSocket(jwtConfig = jwtConfig)

    // Thêm WebSocket khác ở đây...
}