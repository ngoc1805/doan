package com.example.Tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object ServiceRooms : IntIdTable("service_rooms") {
    val name = varchar("name", 100)
    val code = varchar("code", 100)
    val accountId = reference("account_id", Accounts, onDelete = ReferenceOption.CASCADE)
    val address = varchar("address", 100)
    val examPrice = integer("exam_price")
}