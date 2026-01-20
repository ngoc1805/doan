package com.example.dao

import com.example.Tables.Orders
import com.example.utils.toKotlinxInstant
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class OrderDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<OrderDAO>(Orders)

    var userId by Orders.userId
    var phone by Orders.phone
    var address by Orders.address
    var note by Orders.note
    var status by Orders.status
    var imageUrl by Orders.imageUrl
    var createdAt by Orders.createdAt

    fun toModel(): com.example.models.Order {
        return com.example.models.Order(
            id = id.value,
            userId = userId.value,
            phone = phone,
            address = address,
            note = note,
            status = status,
            imageUrl = imageUrl,
            createdAt = createdAt.toKotlinxInstant()
        )
    }
}