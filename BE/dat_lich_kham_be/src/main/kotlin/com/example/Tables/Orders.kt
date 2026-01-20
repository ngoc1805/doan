package com.example.Tables

import com.example.Tables.Inpatients.defaultExpression
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.timestamp

object Orders: IntIdTable("orders") {
    val userId = reference("user_id", Users, onDelete = ReferenceOption.CASCADE)
    val phone = varchar("phone", 255)
    val address = varchar("address", 255)
    val note = text("note")
    val status = varchar("status", 50)
    val imageUrl = varchar("image_url", 255)
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp())
}