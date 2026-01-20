package com.example.service

import com.example.dto.Request.ServiceAppointmentRequest
import com.example.dto.Response.ServiceAppointmentItem
import com.example.dto.Response.ServiceRoomItem
import com.example.models.ServiceAppointment
import com.example.repository.ServiceAppointmentRepository
import kotlinx.datetime.LocalDate

class ServiceAppointmentService(
    private val repo: ServiceAppointmentRepository = ServiceAppointmentRepository()
) {
    fun createServiceAppointment(request: ServiceAppointmentRequest): ServiceAppointment {
        return repo.createServiceAppointment(request)
    }

    fun getServiceRoomIdsByAppointmentId(appointmentId: Int): List<Int> {
        return repo.getServiceRoomIdsByAppointmentId(appointmentId)
    }

    fun getServiceRoomsByAppointmentId(appointmentId: Int): List<ServiceRoomItem> {
        return repo.getServiceRoomsByAppointmentId(appointmentId)
    }

    fun getServiceAppointmentsByRoomAndStatus(
        serviceRoomId: Int,
        status: String?,
        appointmentStatus: String?,
        examDate: LocalDate?
    ): List<ServiceAppointmentItem> {
        return repo.getServiceAppointmentsByRoomAndStatus(serviceRoomId, status, appointmentStatus, examDate)
    }

    fun updateServiceAppointmentStatus(serviceAppointmentId: Int, status: String): Boolean {
        return repo.updateServiceAppointmentStatus(serviceAppointmentId, status)
    }
}