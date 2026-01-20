package com.example.service

import com.example.models.OrderItem
import com.example.repository.OrderItemRepository

class OrderItemService(
    private val repo: OrderItemRepository = OrderItemRepository()
) {
    fun createOrderItem(orderId: Int, menuId: Int, quantity: Int): OrderItem {
        return repo.createOrderItem(orderId, menuId, quantity)
    }
}