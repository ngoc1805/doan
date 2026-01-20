// File: com/example/websocket/handlers/NotificationWebSocketHandler.kt
package com.example.websocket.handlers

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.config.JWTConfig
import com.example.dto.Response.NotificationItem
import com.example.dto.Response.NotificationListResponse
import com.example.service.NotificationService
import com.example.websocket.WebSocketManager
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.serialization.json.*
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("NotificationWebSocket")

/**
 * Verify JWT token
 */
fun verifyJWTToken(token: String, jwtConfig: JWTConfig): Int? {
    return try {
        val verifier = JWT
            .require(Algorithm.HMAC256(jwtConfig.secret))
            .withAudience(jwtConfig.audience)
            .withIssuer(jwtConfig.domain)
            .build()

        val decodedToken = verifier.verify(token)
        decodedToken.getClaim("id").asInt()
    } catch (e: Exception) {
        logger.error("JWT verification failed", e)
        null
    }
}

fun Route.notificationWebSocket(
    service: NotificationService = NotificationService(),
    jwtConfig: JWTConfig? = null
) {
    webSocket("/ws/notifications") {
        val sessionId = this.hashCode()
        logger.info("[$sessionId] WebSocket session created")

        var currentUserId: Int? = null
        var connectionKey: String? = null
        var isAuthenticated = false

        try {
            logger.info("[$sessionId] Starting to listen for incoming frames...")

            for (frame in incoming) {
                if (frame is Frame.Text) {
                    val text = frame.readText()
                    logger.info("[$sessionId] Received: $text")

                    try {
                        val jsonObject = Json.parseToJsonElement(text).jsonObject
                        val type = jsonObject["type"]?.jsonPrimitive?.content

                        when (type) {
                            "connect" -> {
                                if (isAuthenticated) {
                                    logger.warn("[$sessionId] Already authenticated, ignoring duplicate connect")
                                    break
                                }

                                val userId = jsonObject["userId"]?.jsonPrimitive?.int
                                var token = jsonObject["token"]?.jsonPrimitive?.content

                                logger.info("[$sessionId] Connect attempt - userId: $userId")

                                // FIX: Nếu client không gửi token, lấy từ cookie
                                if (token.isNullOrBlank()) {
                                    logger.info("[$sessionId] Token not in message, checking cookies...")
                                    token = call.request.cookies["access_token"]
                                    logger.info("[$sessionId] Token from cookie: ${if (token.isNullOrBlank()) "not found" else "found"}")
                                }

                                if (token.isNullOrBlank()) {
                                    logger.error("[$sessionId] No token provided")
                                    send(Frame.Text("{\"type\":\"error\",\"message\":\"No token provided\"}"))
                                    break
                                }

                                // Verify token
                                val tokenUserId = jwtConfig?.let { verifyJWTToken(token, it) }

                                if (tokenUserId == null) {
                                    logger.error("[$sessionId] Invalid token")
                                    send(Frame.Text("{\"type\":\"error\",\"message\":\"Invalid token\"}"))
                                    break
                                }

                                // FIX: Nếu client gửi userId khác với token, dùng userId từ token
                                if (userId != null && userId != tokenUserId) {
                                    logger.warn("[$sessionId] userId mismatch: $userId (client) != $tokenUserId (token), using token userId")
                                }

                                isAuthenticated = true
                                currentUserId = tokenUserId
                                connectionKey = "notification_$tokenUserId"
                                WebSocketManager.addConnection(connectionKey!!, this)
                                logger.info("[$connectionKey] Authenticated successfully")

                                val page = jsonObject["page"]?.jsonPrimitive?.int ?: 1
                                val pageSize = jsonObject["pageSize"]?.jsonPrimitive?.int ?: 30

                                val startTime = System.currentTimeMillis()
                                logger.info("[$connectionKey] Fetching notifications...")

                                val notifications = service.getNotificationsByUserIdPaged(
                                    tokenUserId,
                                    page,
                                    pageSize
                                )

                                val elapsed = System.currentTimeMillis() - startTime
                                logger.info("[$connectionKey] DB query took ${elapsed}ms, got ${notifications.size} records")

                                val notificationItems = notifications.map { noti ->
                                    NotificationItem(
                                        id = noti.id,
                                        content = noti.content,
                                        isSeen = noti.isSeen,
                                        createdAt = noti.createdAt,
                                        path = noti.path
                                    )
                                }

                                val response = NotificationListResponse(notificationItems)
                                send(Frame.Text(Json.encodeToString(NotificationListResponse.serializer(), response)))
                                logger.info("[$connectionKey] Connected successfully")
                            }

                            "load_more" -> {
                                if (!isAuthenticated) {
                                    logger.warn("[$sessionId] Received load_more before authentication")
                                    send(Frame.Text("{\"type\":\"error\",\"message\":\"Not authenticated\"}"))
                                    break
                                }

                                logger.info("[$connectionKey] Load more requested")
                                val page = jsonObject["page"]?.jsonPrimitive?.int ?: 1
                                val pageSize = jsonObject["pageSize"]?.jsonPrimitive?.int ?: 30

                                currentUserId?.let {
                                    val startTime = System.currentTimeMillis()
                                    val notifications = service.getNotificationsByUserIdPaged(it, page, pageSize)
                                    val elapsed = System.currentTimeMillis() - startTime
                                    logger.info("[$connectionKey] Query took ${elapsed}ms")

                                    val notificationItems = notifications.map { noti ->
                                        NotificationItem(
                                            id = noti.id,
                                            content = noti.content,
                                            isSeen = noti.isSeen,
                                            createdAt = noti.createdAt,
                                            path = noti.path
                                        )
                                    }

                                    val response = NotificationListResponse(notificationItems)
                                    send(Frame.Text(Json.encodeToString(NotificationListResponse.serializer(), response)))
                                }
                            }

                            "ping" -> {
                                if (isAuthenticated) {
                                    send(Frame.Text("{\"type\":\"pong\"}"))
                                }
                            }

                            else -> {
                                logger.warn("[$connectionKey] Unknown message type: $type")
                            }
                        }

                    } catch (e: Exception) {
                        logger.error("[$connectionKey] Error processing message", e)
                        send(Frame.Text("{\"type\":\"error\",\"message\":\"${e.message}\"}"))
                    }
                }
            }

            logger.info("[$sessionId] Incoming channel closed")

        } catch (e: ClosedReceiveChannelException) {
            logger.info("[$connectionKey] WebSocket closed normally")
        } catch (e: Exception) {
            logger.error("[$connectionKey] WebSocket error", e)
        } finally {
            connectionKey?.let { key ->
                WebSocketManager.removeConnection(key, this)
                logger.info("[$key] Connection cleaned up")
            }
        }
    }
}