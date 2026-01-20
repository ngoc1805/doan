package com.example.repository

import com.example.Tables.Appointments
import com.example.Tables.Doctors
import com.example.Tables.Users
import com.example.dao.*
import com.example.dto.Request.AppointmentRequest
import com.example.dto.Response.AppointmentByDoctorIdItem
import com.example.dto.Response.AppointmentItem
import com.example.models.Appointment
import com.example.utils.EncryptionUtil
import com.example.utils.toJavaLocalDate
import com.example.utils.toKotlinxLocalDate
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.toJavaLocalTime
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.and
import java.time.LocalDate as JavaLocalDate
import java.time.LocalTime as JavaLocalTime

class AppointmentRepository {
    fun getFreeTimeSlots(doctorId: Int, date: LocalDate, slots: List<LocalTime>): List<LocalTime> = transaction {
        val busyTimes = AppointmentsDAO.find {
            (Appointments.doctorId eq doctorId) and
                    (Appointments.examDate eq date.toJavaLocalDate()) and
                    (Appointments.status inList listOf(
                        "Đã lên lịch",
                        "Đã thanh toán",
                        "Đang thanh toán",
                        //"Đã hoàn tất"
                    ))
        }.map { LocalTime.parse(it.examTime.toString()) }

        slots.filter { slot -> busyTimes.none { it == slot } }
    }
    //
    fun createAppointment(request: AppointmentRequest): Appointment = transaction {
        val appointment = AppointmentsDAO.new {
            userId = EntityID(request.userId, Users)
            doctorId = EntityID(request.doctorId, Doctors)
            examDate = request.examDate.toJavaLocalDate()
            examTime = request.examTime.toJavaLocalTime()
            status = request.status
            createdAt = java.time.Instant.now()
        }
        appointment.toModel()
    }
    //
    fun getAppointmentsByUserIdAndStatus(userId: Int, statusList: List<String>?): List<AppointmentItem> = transaction {
        val query = if (statusList.isNullOrEmpty()) {
            AppointmentsDAO.find { Appointments.userId eq userId }
        } else {
            AppointmentsDAO.find {
                (Appointments.userId eq userId) and (Appointments.status inList statusList)
            }
        }

        query.map { appointmentDao ->
            val doctorDao = DoctorDAO.findById(appointmentDao.doctorId.value)
            val departmentDao = doctorDao?.departmentId?.let { DepartmentDAO.findById(it.value) }

            AppointmentItem(
                id = appointmentDao.id.value,
                doctorId = doctorDao?.id?.value ?: 0,
                doctorName = doctorDao?.name ?: "",
                doctorCode = doctorDao?.code ?: "",
                department = departmentDao?.name ?: "",
                examPrice = doctorDao?.examPrice?: 0,
                examDate = kotlinx.datetime.LocalDate.parse(appointmentDao.examDate.toString()),
                examTime = kotlinx.datetime.LocalTime.parse(appointmentDao.examTime.toString()),
                status = appointmentDao.status
            )
        }.sortedWith(
            compareBy<AppointmentItem> { it.examDate }.thenBy { it.examTime }
        )
    }
    //
    fun getAppointmentsByDoctorIdAndDateAndStatus(
        doctorId: Int,
        examDate: LocalDate?,
        statusList: List<String>?
    ): List<AppointmentByDoctorIdItem> = transaction {
        val query = when {
            examDate == null && (statusList == null || statusList.isEmpty()) -> {
                AppointmentsDAO.find { Appointments.doctorId eq doctorId }
            }
            examDate == null -> {
                AppointmentsDAO.find {
                    (Appointments.doctorId eq doctorId) and
                            (Appointments.status inList statusList!!)
                }
            }
            statusList == null || statusList.isEmpty() -> {
                AppointmentsDAO.find {
                    (Appointments.doctorId eq doctorId) and
                            (Appointments.examDate eq examDate.toJavaLocalDate())
                }
            }
            else -> {
                AppointmentsDAO.find {
                    (Appointments.doctorId eq doctorId) and
                            (Appointments.examDate eq examDate.toJavaLocalDate()) and
                            (Appointments.status inList statusList)
                }
            }
        }

        // Sắp xếp theo ngày và giờ khám tăng dần
        query.sortedWith(
            compareBy({ it.examDate }, { it.examTime })
        ).map { appointmentDao ->
            val userDao = UsersDAO.findById(appointmentDao.userId.value)
            val fmctoken = try {
                userDao?.accountId?.let { accountId ->
                    AccountDAO.findById(accountId.value)?.fmctoken
                } ?: ""
            } catch (e: Exception) {
                println("Error getting fmctoken for user ${userDao?.id?.value}: ${e.message}")
                ""
            }

            val decryptedFullName = userDao?.fullName?.let { EncryptionUtil.decrypt(it) } ?: ""
            val decryptedGender = userDao?.gender?.let { EncryptionUtil.decrypt(it) } ?: ""
            val decryptedCccd = userDao?.cccd?.let { EncryptionUtil.decrypt(it) } ?: ""
            val decryptedHometown = userDao?.hometown?.let { EncryptionUtil.decrypt(it) } ?: ""

            AppointmentByDoctorIdItem(
                id = appointmentDao.id.value,
                userId = userDao?.id?.value ?: 0,
                userName = decryptedFullName,
                gender = decryptedGender,
                birthDate = userDao?.birthDate?.toKotlinxLocalDate() ?: kotlinx.datetime.LocalDate.parse("1970-01-01"),
                homeTown = decryptedHometown,
                cccd = decryptedCccd,
                examDate = kotlinx.datetime.LocalDate.parse(appointmentDao.examDate.toString()),
                examTime = kotlinx.datetime.LocalTime.parse(appointmentDao.examTime.toString()),
                status = appointmentDao.status,
                fmctoken = fmctoken
            )
        }
    }
    //
    fun updateAppointmentStatus(appointmentId: Int, status: String): Boolean = transaction {
        val appointmentDao = AppointmentsDAO.findById(appointmentId)
        if (appointmentDao != null) {
            appointmentDao.status = status
            true
        } else {
            false
        }
    }
    //
    fun getNearestUpcomingAppointment(userId: Int): AppointmentItem? = transaction {
        val nowDate = java.time.LocalDate.now()
        val nowTime = java.time.LocalTime.now()

        val appointments = AppointmentsDAO.find { Appointments.userId eq userId }
            .filter { appDao ->
                val appDate = java.time.LocalDate.parse(appDao.examDate.toString())
                val appTime = java.time.LocalTime.parse(appDao.examTime.toString())
                // Loại bỏ lịch "Đã hủy"
                appDao.status != "Đã hủy" && (appDate.isAfter(nowDate) || appDate.isEqual(nowDate))
            }
            .sortedWith(compareBy({ it.examDate }, { it.examTime }))

        val appointmentDao = appointments.firstOrNull() ?: return@transaction null
        val doctorDao = DoctorDAO.findById(appointmentDao.doctorId.value)
        val departmentDao = doctorDao?.departmentId?.let { DepartmentDAO.findById(it.value) }

        AppointmentItem(
            id = appointmentDao.id.value,
            doctorId = doctorDao?.id?.value ?: 0,
            doctorName = doctorDao?.name ?: "",
            doctorCode = doctorDao?.code ?: "",
            department = departmentDao?.name ?: "",
            examPrice = doctorDao?.examPrice ?: 0,
            examDate = kotlinx.datetime.LocalDate.parse(appointmentDao.examDate.toString()),
            examTime = kotlinx.datetime.LocalTime.parse(appointmentDao.examTime.toString()),
            status = appointmentDao.status
        )
    }
    //
}