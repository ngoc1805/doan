package com.example.Tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.timestamp

object MealSubscriptionCycles : IntIdTable("meal_subscription_cycles") {
    val inpatientId = reference("inpatient_id", Inpatients, onDelete = ReferenceOption.CASCADE)
    val cycleNumber = integer("cycle_number") // Chu kỳ thứ mấy: 1, 2, 3...
    val weekStartDate = date("week_start_date") // Thứ 2 đầu tuần
    val weekEndDate = date("week_end_date") // Chủ nhật cuối tuần
    val registrationDate = date("registration_date") // Ngày đăng ký/gia hạn
    val status = varchar("status", 50) // pending_payment, active, expired, cancelled, completed
    val daysInCycle = integer("days_in_cycle").default(5) // Số ngày T2-T6
    val expectedMealDays = integer("expected_meal_days").default(5) // Số ngày dự kiến ăn
    val actualMealDays = integer("actual_meal_days").default(0) // Số ngày thực tế ăn
    val actualSkipDays = integer("actual_skip_days").default(0) // Số ngày cắt cơm
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp())
    val updatedAt = timestamp("updated_at").defaultExpression(CurrentTimestamp())

    init {
        uniqueIndex(inpatientId, cycleNumber) // Mỗi inpatient không trùng cycle_number
    }
}