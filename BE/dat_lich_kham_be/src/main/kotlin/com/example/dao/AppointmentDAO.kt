package com.example.dao

import com.example.Tables.Appointments
import com.example.utils.toKotlinxInstant
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import kotlinx.datetime.Instant

class AppointmentsDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<AppointmentsDAO>(Appointments)

    var userId by Appointments.userId
    var doctorId by Appointments.doctorId
    var examDate by Appointments.examDate
    var examTime by Appointments.examTime
    var status by Appointments.status
    var createdAt by Appointments.createdAt // kiểu java.time.Instant

    fun toModel(): com.example.models.Appointment {
        return com.example.models.Appointment(
            id = id.value,
            userId = userId.value,
            doctorId = doctorId.value,
            examDate = kotlinx.datetime.LocalDate.parse(examDate.toString()),
            examTime = kotlinx.datetime.LocalTime.parse(examTime.toString()),
            status = status,
            createdAt = createdAt.toKotlinxInstant()
        )
    }
}