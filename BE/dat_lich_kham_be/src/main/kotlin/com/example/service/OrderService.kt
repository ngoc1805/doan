package com.example.service

import com.example.dao.UsersDAO
import com.example.dto.Response.OrderWithItemsResponse
import com.example.models.Order
import com.example.repository.OrderRepository
import org.jetbrains.exposed.dao.id.EntityID

class OrderService(private val repo: OrderRepository = OrderRepository()) {
    fun createOrder(
        userId: Int,
        phone: String,
        address: String,
        note: String,
        status: String
    ): Order {
        return repo.createOrder(userId, phone,address, note, status)
    }

    fun getOrdersWithItemsByStatuses(statusList: List<String>): List<OrderWithItemsResponse> {
        return repo.getOrdersWithItemsByStatuses(statusList)
    }

    fun getOrdersWithItemsByStatusesAndUserId(statusList: List<String>, userId: Int): List<OrderWithItemsResponse> {
        return repo.getOrdersWithItemsByStatusesAndUserId(statusList, userId)
    }
    //
    fun getOrderById(orderId: Int): Order? {
        return repo.getOrderById(orderId)
    }
    fun getUserByOrderId(orderId: Int): UsersDAO? {
        return repo.getUserByOrderId(orderId)
    }
    fun updateOrderStatusAndImage(orderId: Int, status: String, imageUrl: String): Boolean {
        return repo.updateOrderStatusAndImage(orderId, status, imageUrl)
    }
    fun getOrderTotalPrice(orderId: Int): Int {
        return repo.getOrderTotalPrice(orderId)
    }
    fun increaseUserBalance(userId: Int, amount: Int): Boolean {
        return repo.increaseUserBalance(userId, amount)
    }
    //
}