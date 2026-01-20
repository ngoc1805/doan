package com.example.dao

import com.example.Tables.OrderItem
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class OrderItemDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<OrderItemDAO>(OrderItem)

    var orderId by OrderItem.orderId
    var menuId by OrderItem.menuId
    var quantity by OrderItem.quantity

    fun toModel(): com.example.models.OrderItem {
        return com.example.models.OrderItem(
            id = id.value,
            orderId = orderId.value,
            menuId = menuId.value,
            quantity = quantity
        )
    }
}