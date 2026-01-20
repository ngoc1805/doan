package com.example.Tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.timestamp

object Transactions : IntIdTable("transactions") {
    val userId = reference("user_id", Users, onDelete = ReferenceOption.CASCADE)
    val category = varchar("category", 50)
    val transactionType = text("transaction_type")          // Mô tả chi tiết (mã hóa)
    val amount = varchar("amount", 50)
    val isIncome = bool("is_income")                        // true = thu, false = chi
    val transactionTime = timestamp("transaction_time").defaultExpression(CurrentTimestamp())
}