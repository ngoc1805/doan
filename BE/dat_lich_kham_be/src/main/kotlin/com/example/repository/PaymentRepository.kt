package com.example.repository

import com.example.Tables.ServiceAppointments
import com.example.dao.AppointmentsDAO
import com.example.dao.DoctorDAO
import com.example.dao.ServiceAppointmenDAO
import com.example.dao.ServiceRoomDAO
import org.jetbrains.exposed.sql.transactions.transaction

class PaymentRepository {

    fun calculateTotalPayment(appointmentId: Int): Int? = transaction {
        // Lấy thông tin appointment
        val appointmentDao = AppointmentsDAO.findById(appointmentId) ?: return@transaction null

        // Lấy thông tin bác sĩ để lấy giá khám
        val doctorDao = DoctorDAO.findById(appointmentDao.doctorId.value) ?: return@transaction null
        val doctorExamPrice = doctorDao.examPrice

        // Tính tiền khám bác sĩ còn thiếu (giá gốc - 100,000)
        val remainingDoctorFee = if (doctorExamPrice > 100000) {
            doctorExamPrice - 100000
        } else {
            0
        }

        // Lấy danh sách các phòng dịch vụ của appointment này
        val serviceAppointments = ServiceAppointmenDAO.find {
            ServiceAppointments.appointmentId eq appointmentId
        }

        // Tính tổng tiền các phòng dịch vụ
        val totalServiceRoomFee = serviceAppointments.sumOf { serviceAppointmentDao ->
            val serviceRoom = ServiceRoomDAO.findById(serviceAppointmentDao.serviceRoomId.value)
            serviceRoom?.examPrice ?: 0
        }

        // Tổng tiền = tiền bác sĩ còn thiếu + tổng tiền phòng dịch vụ
        val totalPayment = remainingDoctorFee + totalServiceRoomFee

        return@transaction totalPayment
    }
}