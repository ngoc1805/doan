package com.example.Tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object Menus : IntIdTable("menus") {
    val name = varchar("name", 100)
    val examPrice = integer("exam_price")
    val description = text("description") // hoặc varchar("description", 255) nếu mô tả ngắn
    val category = varchar("category", 50)
    val isDisplay = bool("is_display").default(false)
    val imageUrl = varchar("image_url", 255)
}