package com.example.Tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object Results : IntIdTable("results") {
    val appointmentId = reference("appointment_id", Appointments, onDelete = ReferenceOption.CASCADE)
    val comment = text("comment")
    val dietRecommendation = text("diet_recommendation").nullable() // Chỉ định ăn uống
}