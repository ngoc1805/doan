package com.example.Tables

import com.example.Tables.Roles.default
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.time
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant

object Appointments : IntIdTable("appointments") {
    val userId = reference("user_id", Users, onDelete = ReferenceOption.CASCADE)   // FK đến Users
    val doctorId = reference("doctor_id", Doctors, onDelete = ReferenceOption.CASCADE) // FK đến Doctors
    val examDate = date("exam_date")          // Ngày khám
    val examTime = time("exam_time")          // Giờ khám
    val status = varchar("status", 50)        // Trạng thái (vd: "pending", "completed", "cancelled")
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp())
}