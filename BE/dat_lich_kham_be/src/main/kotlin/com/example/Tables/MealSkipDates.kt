package com.example.Tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.timestamp

object MealSkipDates : IntIdTable("meal_skip_dates") {
    val cycleId = reference("cycle_id", MealSubscriptionCycles, onDelete = ReferenceOption.CASCADE)
    val inpatientId = reference("inpatient_id", Inpatients, onDelete = ReferenceOption.CASCADE)
    val skipDate = date("skip_date") // Ngày cắt cơm
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp())

    init {
        uniqueIndex(cycleId, skipDate) // Mỗi cycle không cắt trùng ngày
    }
}