package com.example.dao

import com.example.Tables.ServiceRooms
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class ServiceRoomDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ServiceRoomDAO>(ServiceRooms)

    var name by ServiceRooms.name
    var code by ServiceRooms.code
    var accountId by ServiceRooms.accountId
    var address by ServiceRooms.address
    var examPrice by ServiceRooms.examPrice

    fun toModel(): com.example.models.ServiceRoom{
        return com.example.models.ServiceRoom(
            id = id.value,
            name = name,
            code = code,
            accountId = accountId.value,
            address = address,
            examPrice = examPrice
        )
    }
}