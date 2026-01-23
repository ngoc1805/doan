package com.example.dao

import com.example.Tables.Inpatients
import com.example.utils.toKotlinxInstant
import com.example.utils.toKotlinxLocalDate
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class InpatientDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<InpatientDAO>(Inpatients)

    var userId by Inpatients.userId
    var appointmentId by Inpatients.appointmentId
    var address by Inpatients.address
    var admissionDate by Inpatients.admissionDate
    var dischargeDate by Inpatients.dischargeDate
    var status by Inpatients.status
    var createdAt by Inpatients.createdAt

    fun toModel(): com.example.models.Inpatient {
        return com.example.models.Inpatient(
            id = id.value,
            userId = userId.value,
            appointmentId = appointmentId?.value,
            address = address,
            admissionDate = admissionDate?.toKotlinxLocalDate(),
            dischargeDate = dischargeDate?.toKotlinxLocalDate(),
            status = status,
            createdAt = createdAt.toKotlinxInstant()
        )
    }
}