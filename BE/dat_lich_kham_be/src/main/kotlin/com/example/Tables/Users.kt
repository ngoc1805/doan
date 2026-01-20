package com.example.Tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.date


object Users : IntIdTable("users") {
    val accountId = reference("account_id", Accounts, onDelete = ReferenceOption.CASCADE) // Khóa ngoại tới Accounts
    val fullName = varchar("full_name", 500)
    val gender = varchar("gender", 200)
    val birthDate = date("birth_date")
    val cccd = varchar("cccd", 300).uniqueIndex()
    val hometown = varchar("hometown", 500)
    val balance = integer("balance").default(0)
    val pincode = varchar("pin_code", 255).nullable()
    val imageurl = varchar("image_url", 255).nullable()
    val phone = varchar("phone", 300).nullable()
}