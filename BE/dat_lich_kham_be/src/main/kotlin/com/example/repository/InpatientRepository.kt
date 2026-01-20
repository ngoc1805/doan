package com.example.repository

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
    fun hasInpatientWithStatus(userId: Int, status: String): Boolean = transaction {
        InpatientDAO.find {
            (Inpatients.userId eq userId) and (Inpatients.status eq status)
        }.empty().not()
    }

    fun getInpatientAddress(userId: Int, status: String): String? = transaction {
        InpatientDAO.find {
            (Inpatients.userId eq userId) and (Inpatients.status eq status)
        }.firstOrNull()?.address
    }

    fun createInpatient(userId: Int): Int = transaction {
        val inpatient = InpatientDAO.new {
            this.userId = EntityID(userId, Inpatients)
            this.status = "Đang chờ"
        }
        inpatient.id.value
    }

    fun getInpatientsByStatus(status: String): InpatientListResponse = transaction {
        val inpatients = InpatientDAO.find { Inpatients.status eq status }
            .map { inpatientDao ->
                val userDao = UsersDAO.findById(inpatientDao.userId.value)

                // Giải mã tất cả thông tin nhạy cảm từ user
                val decryptedFullName = userDao?.fullName?.let { EncryptionUtil.decrypt(it) } ?: ""
                val decryptedGender = userDao?.gender?.let { EncryptionUtil.decrypt(it) } ?: ""
                val decryptedCccd = userDao?.cccd?.let { EncryptionUtil.decrypt(it) } ?: ""
                val decryptedHometown = userDao?.hometown?.let { EncryptionUtil.decrypt(it) } ?: ""

                InpatientItem(
                    id = inpatientDao.id.value,
                    userId = inpatientDao.userId.value,
                    fullname = decryptedFullName,
                    gender = decryptedGender,
                    birthDate = userDao?.birthDate?.toKotlinxLocalDate() ?: LocalDate.parse("1970-01-01"),
                    cccd = decryptedCccd,
                    hometown = decryptedHometown,
                    address = inpatientDao.address ?: "",
                    status = inpatientDao.status,
                    createAt = inpatientDao.createdAt.toKotlinxInstant()
                )
            }
        InpatientListResponse(inpatients)
    }

    fun dischargeInpatientById(id: Int): Boolean = transaction {
        val inpatient = InpatientDAO.findById(id)
        if (inpatient == null) return@transaction false
        inpatient.status = "Đã xuất viện"
        true
    }

    fun updateAddressAndAdmit(id: Int, address: String): Boolean = transaction {
        val inpatient = InpatientDAO.findById(id)
        if (inpatient == null) return@transaction false
        inpatient.address = address
        inpatient.status = "Đã nhập viện"
        true
    }
}