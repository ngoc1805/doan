package com.example.Tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.date

object ServiceAppointments : IntIdTable("service_appointments") {
    val appointmentId = reference("appointment_id", Appointments, onDelete = ReferenceOption.CASCADE)
    val serviceRoomId = reference("service_room_id", ServiceRooms, onDelete = ReferenceOption.CASCADE)
    val status = varchar("status", 50)
    val examDate = date("exam_date")
}