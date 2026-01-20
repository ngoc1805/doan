package com.example.repository

import com.example.Tables.OrderItem
import com.example.Tables.Orders
import com.example.Tables.Users
import com.example.dao.*
import com.example.dto.Response.OrderItemDetail
import com.example.dto.Response.OrderWithItemsResponse
import com.example.models.Order
import com.example.utils.EncryptionUtil
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

class OrderRepository {
    fun createOrder(
        userId: Int,
        phone: String,
        address: String,
        note: String,
        status: String
    ): Order = transaction {
        val orderDao = OrderDAO.new {
            this.userId = EntityID(userId, Users)
            this.phone = phone
            this.address = address
            this.note = note
            this.status = status
            this.imageUrl = ""
        }
        orderDao.toModel()
    }

    fun getOrdersWithItemsByStatuses(statusList: List<String>): List<OrderWithItemsResponse> = transaction {
        OrderDAO.find { Orders.status inList statusList }
            .sortedBy { it.id.value }
            .map { order ->
                val user = UsersDAO.findById(order.userId.value)
                val account = user?.accountId?.let { AccountDAO.findById(it.value) }
                val items = OrderItemDAO.find { OrderItem.orderId eq order.id.value }.map { item ->
                    val menu = MenuDAO.findById(item.menuId.value)
                    OrderItemDetail(
                        menuName = menu?.name ?: "",
                        quantity = item.quantity
                    )
                }
                val totalPrice = OrderItemDAO.find { OrderItem.orderId eq order.id.value }.sumOf { item ->
                    val menu = MenuDAO.findById(item.menuId.value)
                    (menu?.examPrice ?: 0) * item.quantity
                }
                // Giải mã fullName
                val decryptedFullName = user?.fullName?.let { EncryptionUtil.decrypt(it) } ?: ""

                OrderWithItemsResponse(
                    id = order.id.value,
                    userId = user?.id?.value ?: 0,
                    userName = decryptedFullName,
                    fmctoken = account?.fmctoken ?: "",
                    phone = order.phone,
                    address = order.address,
                    note = order.note,
                    status = order.status,
                    items = items,
                    totalPrice = totalPrice,
                    imageUrl = order.imageUrl
                )
            }
    }

    fun getOrdersWithItemsByStatusesAndUserId(
        statusList: List<String>,
        userId: Int
    ): List<OrderWithItemsResponse> = transaction {
        OrderDAO.find { (Orders.status inList statusList) and (Orders.userId eq userId) }
            .sortedBy { it.id.value }
            .map { order ->
                val user = UsersDAO.findById(order.userId.value)
                val account = user?.accountId?.let { AccountDAO.findById(it.value) }
                val items = OrderItemDAO.find { OrderItem.orderId eq order.id.value }.map { item ->
                    val menu = MenuDAO.findById(item.menuId.value)
                    OrderItemDetail(
                        menuName = menu?.name ?: "",
                        quantity = item.quantity
                    )
                }
                val totalPrice = OrderItemDAO.find { OrderItem.orderId eq order.id.value }.sumOf { item ->
                    val menu = MenuDAO.findById(item.menuId.value)
                    (menu?.examPrice ?: 0) * item.quantity
                }
                // Giải mã fullName
                val decryptedFullName = user?.fullName?.let { EncryptionUtil.decrypt(it) } ?: ""

                OrderWithItemsResponse(
                    id = order.id.value,
                    userId = user?.id?.value ?: 0,
                    userName = decryptedFullName,
                    fmctoken = account?.fmctoken ?: "",
                    phone = order.phone,
                    address = order.address,
                    note = order.note,
                    status = order.status,
                    items = items,
                    totalPrice = totalPrice,
                    imageUrl = order.imageUrl
                )
            }
    }

    fun getOrderById(orderId: Int): Order? = transaction {
        OrderDAO.findById(orderId)?.toModel()
    }

    fun getUserByOrderId(orderId: Int): UsersDAO? = transaction {
        val order = OrderDAO.findById(orderId)
        order?.let { UsersDAO.findById(order.userId.value) }
    }

    fun updateOrderStatusAndImage(orderId: Int, status: String, imageUrl: String): Boolean = transaction {
        val order = OrderDAO.findById(orderId)
        if (order != null) {
            order.status = status
            order.imageUrl = imageUrl
            true
        } else false
    }

    fun getOrderTotalPrice(orderId: Int): Int = transaction {
        OrderItemDAO.find { OrderItem.orderId eq orderId }
            .sumOf { item ->
                val menu = MenuDAO.findById(item.menuId.value)
                (menu?.examPrice ?: 0) * item.quantity
            }
    }

    fun increaseUserBalance(userId: Int, amount: Int): Boolean = transaction {
        val user = UsersDAO.findById(userId)
        if (user != null) {
            user.balance += amount
            true
        } else false
    }
}