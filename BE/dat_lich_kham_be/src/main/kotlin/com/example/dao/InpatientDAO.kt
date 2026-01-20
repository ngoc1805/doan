package com.example.dao

import com.example.Tables.Appointments
import com.example.Tables.Inpatients
import com.example.utils.toKotlinxInstant
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class InpatientDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<InpatientDAO>(Inpatients)

    var userId by Inpatients.userId
    var address by Inpatients.address
    var status by Inpatients.status
    var createdAt by Inpatients.createdAt

    fun toModel() : com.example.models.Inpatient{
        return com.example.models.Inpatient(
            id = id.value,
            userId = userId.value,
            address = address ,
            status = status,
            createdAt = createdAt.toKotlinxInstant()
        )
    }
}