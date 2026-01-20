package com.example.utils

import io.ktor.websocket.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import com.example.controller.appointmentSessions

suspend fun notifyAppointmentChanged(doctorId: Int, date: String, slot: String) {
    val message = Json.encodeToString(
        mapOf(
            "type" to "busySlot",
            "doctorId" to doctorId,
            "date" to date,
            "slot" to slot
        )
    )
    for (session in appointmentSessions.values) {
        try {
            session.send(Frame.Text(message))
        } catch (e: Exception) {
            // Nếu client đã disconnect, có thể remove session ở đây
        }
    }
}