package com.example.repository

import com.example.dao.OrderItemDAO
import com.example.models.OrderItem
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction
import com.example.Tables.Orders
import com.example.Tables.Menus

class OrderItemRepository {
    fun createOrderItem(orderId: Int, menuId: Int, quantity: Int): OrderItem = transaction {
        val orderItemDao = OrderItemDAO.new {
            this.orderId = EntityID(orderId, Orders)
            this.menuId = EntityID(menuId, Menus)
            this.quantity = quantity
        }
        orderItemDao.toModel()
    }
}