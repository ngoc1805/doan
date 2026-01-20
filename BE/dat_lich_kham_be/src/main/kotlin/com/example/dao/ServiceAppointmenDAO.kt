package com.example.dao


import com.example.Tables.ServiceAppointments
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class ServiceAppointmenDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ServiceAppointmenDAO>(ServiceAppointments)

    var appointmentId by ServiceAppointments.appointmentId
    var serviceRoomId by ServiceAppointments.serviceRoomId
    var status by ServiceAppointments.status
    var examDate by ServiceAppointments.examDate

    fun toModel(): com.example.models.ServiceAppointment{
        return com.example.models.ServiceAppointment(
            id = id.value,
            appointmentId = appointmentId.value,
            serviceRoomId = serviceRoomId.value,
            status = status,
            examDate = kotlinx.datetime.LocalDate.parse(examDate.toString()),

        )
    }
}