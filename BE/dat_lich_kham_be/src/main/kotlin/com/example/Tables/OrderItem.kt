package com.example.Tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object OrderItem: IntIdTable("order_item") {
    val orderId = reference("order_id", Orders, onDelete = ReferenceOption.CASCADE)
    val menuId = reference("menu_id", Menus, onDelete = ReferenceOption.CASCADE)
    val quantity = integer("quantity")
}