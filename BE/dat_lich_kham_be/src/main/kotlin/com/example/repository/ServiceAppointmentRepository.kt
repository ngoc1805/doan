package com.example.repository

import com.example.Tables.Appointments
import com.example.Tables.ServiceAppointments
import com.example.Tables.ServiceRooms
import com.example.dao.*
import com.example.dto.Request.ServiceAppointmentRequest
import com.example.dto.Response.ServiceAppointmentItem
import com.example.dto.Response.ServiceRoomItem
import com.example.models.ServiceAppointment
import com.example.utils.EncryptionUtil
import com.example.utils.toJavaLocalDate
import com.example.utils.toKotlinxLocalDate
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class ServiceAppointmentRepository {
    fun createServiceAppointment(request: ServiceAppointmentRequest): ServiceAppointment {
        return transaction {
            val dao = ServiceAppointmenDAO.new {
                appointmentId = EntityID(request.appointmentId, Appointments)
                serviceRoomId = EntityID(request.serviceRoomId, ServiceRooms)
                status = request.status
                examDate = request.examDate.toJavaLocalDate()
            }
            dao.toModel()
        }
    }

    //
    fun getServiceRoomIdsByAppointmentId(appointmentId: Int): List<Int> {
        return transaction {
            ServiceAppointmenDAO.find {
                com.example.Tables.ServiceAppointments.appointmentId eq appointmentId
            }.map { it.serviceRoomId.value }
        }
    }

    fun getServiceRoomsByAppointmentId(appointmentId: Int): List<ServiceRoomItem> = transaction {
        ServiceAppointmenDAO.find { ServiceAppointments.appointmentId eq appointmentId }
            .mapNotNull { saDao ->
                ServiceRoomDAO.findById(saDao.serviceRoomId.value)?.let { roomDao ->
                    ServiceRoomItem(
                        id = roomDao.id.value,
                        name = roomDao.name,
                        address = roomDao.address,
                        code = roomDao.code,
                        examPrice = roomDao.examPrice
                    )
                }
            }
    }
    //
    fun getServiceAppointmentsByRoomAndStatus(
        serviceRoomId: Int,
        status: String?,
        appointmentStatus: String?,
        examDate: LocalDate?
    ): List<ServiceAppointmentItem> = transaction {
        // Tìm các lịch hẹn dịch vụ theo phòng và status
        val query = ServiceAppointmenDAO.find {
            var expr = ServiceAppointments.serviceRoomId eq serviceRoomId
            if (!status.isNullOrBlank()) {
                expr = expr and (ServiceAppointments.status eq status)
            }
            if (examDate != null) {
                expr = expr and (ServiceAppointments.examDate eq examDate.toJavaLocalDate())
            }
            expr
        }.toList()

        // Nếu cần lọc theo trạng thái của lịch khám tổng
        val filtered = if (!appointmentStatus.isNullOrBlank()) {
            query.filter { saDao ->
                val appDao = AppointmentsDAO.findById(saDao.appointmentId.value)
                appDao != null && appDao.status == appointmentStatus
            }
        } else {
            query
        }

        // Map sang response
        filtered.mapNotNull { saDao ->
            val appDao = AppointmentsDAO.findById(saDao.appointmentId.value)
            val userDao = appDao?.userId?.let { UsersDAO.findById(it.value) }
            val fmctoken = try {
                userDao?.accountId?.let { accountId ->
                    AccountDAO.findById(accountId.value)?.fmctoken
                } ?: ""
            } catch (e: Exception) {
                ""
            }

            val decryptedFullName = userDao?.fullName?.let { EncryptionUtil.decrypt(it) } ?: ""
            val decryptedGender = userDao?.gender?.let { EncryptionUtil.decrypt(it) } ?: ""
            val decryptedCccd = userDao?.cccd?.let { EncryptionUtil.decrypt(it) } ?: ""
            val decryptedHometown = userDao?.hometown?.let { EncryptionUtil.decrypt(it) } ?: ""

            if (appDao == null || userDao == null) null else ServiceAppointmentItem(
                id = saDao.id.value,
                appointmentId = appDao.id.value,
                userId = userDao.id.value,
                userName = decryptedFullName,
                gender = decryptedGender,
                birthDate = userDao.birthDate?.toKotlinxLocalDate() ?: LocalDate.parse("1970-01-01"),
                homeTown = decryptedHometown,
                cccd = decryptedCccd,
                examDate = kotlinx.datetime.LocalDate.parse(saDao.examDate.toString()),
                examTime = appDao.examTime?.let { kotlinx.datetime.LocalTime.parse(it.toString()) } ?: LocalTime.parse("00:00"),
                status = saDao.status,
                fmctoken = fmctoken
            )
        }
    }
    //
    fun updateServiceAppointmentStatus(serviceAppointmentId: Int, status: String): Boolean = transaction {
        val dao = ServiceAppointmenDAO.findById(serviceAppointmentId)
        if (dao != null) {
            dao.status = status
            true
        } else {
            false
        }
    }
}