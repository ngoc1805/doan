package com.example.Tables

import com.example.Tables.Users.default
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object Doctors : IntIdTable("doctors") {
    val name = varchar("name", 100)
    val code = varchar("code", 100)
    val accountId = reference("account_id", Accounts, onDelete = ReferenceOption.CASCADE)
    val departmentId = reference("department_id", Departments, onDelete = ReferenceOption.CASCADE)
    val examPrice = integer("exam_price")
    val balance = integer("balance").default(0)
}