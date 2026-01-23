package com.example.Tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.timestamp

object Inpatients : IntIdTable("inpatients") {
    val userId = reference("user_id", Users, onDelete = ReferenceOption.CASCADE)
    val appointmentId = reference("appointment_id", Appointments, onDelete = ReferenceOption.SET_NULL).nullable()
    val address = varchar("address", 100).nullable()
    val admissionDate = date("admission_date").nullable() // Ngày nhập viện
    val dischargeDate = date("discharge_date").nullable() // Ngày xuất viện
    val status = varchar("status", 50) // "Đang chờ", "Đã nhập viện", "Đã xuất viện"
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp())
}