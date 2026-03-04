package com.example.repository

import com.example.Tables.Appointments
import com.example.Tables.Inpatients
import com.example.dao.InpatientDAO
import com.example.dao.UsersDAO
import com.example.dto.Response.InpatientItem
import com.example.dto.Response.InpatientListResponse
import com.example.utils.EncryptionUtil
import com.example.utils.toKotlinxInstant
import com.example.utils.toKotlinxLocalDate
import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

class InpatientRepository {

    /**
     * Kiểm tra bệnh nhân có trạng thái cụ thể không
     */
    fun hasInpatientWithStatus(userId: Int, status: String): Boolean = transaction {
        InpatientDAO.find {
            (Inpatients.userId eq userId) and (Inpatients.status eq status)
        }.empty().not()
    }

    /**
     * Lấy địa chỉ nội trú của bệnh nhân theo trạng thái
     */
    fun getInpatientAddress(userId: Int, status: String): String? = transaction {
        InpatientDAO.find {
            (Inpatients.userId eq userId) and (Inpatients.status eq status)
        }.firstOrNull()?.address
    }

    /**
     * Tạo bản ghi nội trú mới
     */
    fun createInpatient(userId: Int, appointmentId: Int? = null): Int = transaction {
        val inpatient = InpatientDAO.new {
            this.userId = EntityID(userId, Inpatients)
            this.appointmentId = appointmentId?.let { EntityID(it, Appointments) }
            this.status = "Đang chờ"
        }
        inpatient.id.value
    }

    /**
     * Lấy danh sách bệnh nhân nội trú theo trạng thái
     */
    fun getInpatientsByStatus(status: String): InpatientListResponse = transaction {
        val inpatients = InpatientDAO.find { Inpatients.status eq status }
            .map { inpatientDao ->
                convertToInpatientItem(inpatientDao)
            }
        InpatientListResponse(inpatients)
    }

    /**
     * Xuất viện - cập nhật trạng thái và ngày xuất viện
     */
    fun dischargeInpatientById(id: Int): Boolean = transaction {
        val inpatient = InpatientDAO.findById(id) ?: return@transaction false
        inpatient.dischargeDate = java.time.LocalDate.now()
        inpatient.status = "Đã xuất viện"
        true
    }

    /**
     * Cập nhật địa chỉ và chuyển sang trạng thái "Đã nhập viện"
     */
    fun updateAddressAndAdmit(id: Int, address: String): Boolean = transaction {
        val inpatient = InpatientDAO.findById(id) ?: return@transaction false
        inpatient.address = address
        inpatient.admissionDate = java.time.LocalDate.now()
        inpatient.status = "Đã nhập viện"
        true
    }

    /**
     * Lấy thông tin nội trú hiện tại (trạng thái "Đã nhập viện")
     */
    fun getCurrentInpatient(userId: Int): InpatientItem? = transaction {
        val inpatientDao = InpatientDAO.find {
            (Inpatients.userId eq userId) and (Inpatients.status eq "Đã nhập viện")
        }.firstOrNull() ?: return@transaction null

        convertToInpatientItem(inpatientDao)
    }

    /**
     * Lấy lịch sử các lần nội trú (trạng thái "Đã xuất viện")
     * Sắp xếp theo ngày xuất viện mới nhất
     */
    fun getInpatientHistory(userId: Int): List<InpatientItem> = transaction {
        InpatientDAO.find {
            (Inpatients.userId eq userId) and (Inpatients.status eq "Đã xuất viện")
        }
            .sortedByDescending { it.dischargeDate }
            .map { inpatientDao ->
                convertToInpatientItem(inpatientDao)
            }
    }

    /**
     * Lấy thông tin chi tiết một bản ghi nội trú theo ID
     */
    fun getInpatientById(id: Int): InpatientItem? = transaction {
        val inpatientDao = InpatientDAO.findById(id) ?: return@transaction null
        convertToInpatientItem(inpatientDao)
    }

    /**
     * Helper function: Chuyển đổi InpatientDAO sang InpatientItem
     * Giải mã các thông tin nhạy cảm
     */
    private fun convertToInpatientItem(inpatientDao: InpatientDAO): InpatientItem {
        val userDao = UsersDAO.findById(inpatientDao.userId.value)

        // Giải mã tất cả thông tin nhạy cảm từ user
        val decryptedFullName = userDao?.fullName?.let { EncryptionUtil.decrypt(it) } ?: ""
        val decryptedGender = userDao?.gender?.let { EncryptionUtil.decrypt(it) } ?: ""
        val decryptedCccd = userDao?.cccd?.let { EncryptionUtil.decrypt(it) } ?: ""
        val decryptedHometown = userDao?.hometown?.let { EncryptionUtil.decrypt(it) } ?: ""

        return InpatientItem(
            id = inpatientDao.id.value,
            userId = inpatientDao.userId.value,
            fullname = decryptedFullName,
            gender = decryptedGender,
            birthDate = userDao?.birthDate?.toKotlinxLocalDate() ?: LocalDate.parse("1970-01-01"),
            cccd = decryptedCccd,
            hometown = decryptedHometown,
            address = inpatientDao.address ?: "",
            admissionDate = inpatientDao.admissionDate?.toKotlinxLocalDate(),
            dischargeDate = inpatientDao.dischargeDate?.toKotlinxLocalDate(),
            status = inpatientDao.status,
            createAt = inpatientDao.createdAt.toKotlinxInstant()
        )
    }
}

/**
 * Tạo bản ghi nội trú mới với thông tin appointment
 */
fun createInpatientWithAppointment(userId: Int, appointmentId: Int?): Int = transaction {
    val inpatient = InpatientDAO.new {
        this.userId = EntityID(userId, Inpatients)
        this.appointmentId = appointmentId?.let { EntityID(it, Appointments) }
        this.status = "Đang chờ"
    }
    inpatient.id.value
}

/**
 * Nhập viện - cập nhật địa chỉ và trạng thái
 */
fun admitInpatient(id: Int, address: String): Boolean = transaction {
    val inpatient = InpatientDAO.findById(id) ?: return@transaction false
    inpatient.address = address
    inpatient.admissionDate = java.time.LocalDate.now()
    inpatient.status = "Đã nhập viện"
    true
}