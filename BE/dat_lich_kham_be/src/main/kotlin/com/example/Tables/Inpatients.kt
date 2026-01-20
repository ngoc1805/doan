package com.example.Tables

import com.example.Tables.Appointments.defaultExpression
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.timestamp

object Inpatients : IntIdTable("inpatients") {
    val userId = reference("user_id", Users, onDelete = ReferenceOption.CASCADE)
    val address = varchar("address", 100).nullable()
    val status = varchar("status", 50)
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp())
}