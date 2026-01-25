package com.example.repository

import com.example.Tables.Appointments
import com.example.Tables.Results
import com.example.Tables.ResultFiles
import com.example.dao.AppointmentsDAO
import com.example.dao.ResultDAO
import com.example.dao.ResultFileDAO
import com.example.dao.UsersDAO
import com.example.dto.Request.ResultRequest
import com.example.dto.Response.ResultItem
import com.example.dto.Response.ResultFileItem
import com.example.utils.EncryptionUtil
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

class ResultRepository {
    fun getResultsByUserIdAndStatus(userId: Int, status: String): List<ResultItem> = transaction {
        val appointments = AppointmentsDAO.find {
            (com.example.Tables.Appointments.userId eq userId) and
                    (com.example.Tables.Appointments.status eq status)
        }.toList()
            .sortedByDescending { it.id.value }

        appointments.mapNotNull { appDao ->
            val resultDao = ResultDAO.find { Results.appointmentId eq appDao.id.value }.firstOrNull()
            val userDao = UsersDAO.findById(appDao.userId.value)
            if (resultDao != null && userDao != null) {
                val resultFiles = ResultFileDAO.find { ResultFiles.appointmentId eq appDao.id.value }
                    .map { fileDao ->
                        ResultFileItem(
                            id = fileDao.id.value,
                            fileName = fileDao.fileName,
                            // ✅ LUÔN TRẢ VỀ ENDPOINT URL
                            // Endpoint sẽ tự động ưu tiên file đã ký nếu có
                            filePath = "/files/${fileDao.id.value}"
                        )
                    }
                val decryptedFullName = EncryptionUtil.decrypt(userDao.fullName) ?: ""
                ResultItem(
                    appointmentId = appDao.id.value,
                    fullName = decryptedFullName,
                    comment = resultDao.comment,
                    dietRecommendation = resultDao.dietRecommendation,
                    resultFiles = resultFiles,
                    examDate = kotlinx.datetime.LocalDate.parse(appDao.examDate.toString())
                )
            } else null
        }
    }

    fun createResult(request: ResultRequest) = transaction {
        ResultDAO.new {
            appointmentId = EntityID(request.appointmentId, Appointments)
            comment = request.comment
            dietRecommendation = request.dietRecommendation
        }
    }
}