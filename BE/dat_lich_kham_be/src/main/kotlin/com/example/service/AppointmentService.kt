package com.example.service

import com.example.dto.Request.AppointmentRequest
import com.example.dto.Request.FreeTimeRequest
import com.example.dto.Response.AppointmentByDoctorIdItem
import com.example.dto.Response.AppointmentItem
import com.example.models.Appointment
import com.example.repository.AppointmentRepository
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

class AppointmentService(
    private val appointmentRepository: AppointmentRepository
) {
    fun getFreeTimeSlots(request: FreeTimeRequest): List<LocalTime> {
        return appointmentRepository.getFreeTimeSlots(request.doctorId, request.date, request.slots)
    }
    //
    fun createAppointment(request: AppointmentRequest): Appointment {
        return appointmentRepository.createAppointment(request)
    }
    //
    fun getAppointmentsByUserIdAndStatus(userId: Int, statusList: List<String>?): List<AppointmentItem> {
        return appointmentRepository.getAppointmentsByUserIdAndStatus(userId, statusList)
    }
    //
    fun getAppointmentsByDoctorIdAndDateAndStatus(
        doctorId: Int,
        examDate: LocalDate?,
        statusList: List<String>?
    ): List<AppointmentByDoctorIdItem> {
        return appointmentRepository.getAppointmentsByDoctorIdAndDateAndStatus(doctorId, examDate, statusList)
    }
    //
    fun updateAppointmentStatus(appointmentId: Int, status: String): Boolean {
        return appointmentRepository.updateAppointmentStatus(appointmentId, status)
    }
    //
    fun getNearestUpcomingAppointment(userId: Int): AppointmentItem? {
        return appointmentRepository.getNearestUpcomingAppointment(userId)
    }
}