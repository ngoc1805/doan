package com.example.repository

import com.example.Tables.Appointments
import com.example.Tables.ResultFiles
import com.example.dao.ResultFileDAO
import com.example.dto.Response.ResultFileItem
import com.example.models.ResultFile
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

class ResultFileRepository {
    fun addResultFile(
        appointmentId: Int,
        fileName: String,
        filePath: String,
        fileType: String
    ): ResultFile = transaction {
        val fileDao = ResultFileDAO.new {
            this.appointmentId = EntityID(appointmentId, Appointments)
            this.fileName = fileName
            this.filePath = filePath
            this.fileType = fileType
        }
        fileDao.toModel()
    }

    fun getFilesByAppointmentId(appointmentId: Int): List<ResultFileItem> = transaction {
        ResultFileDAO.find { ResultFiles.appointmentId eq appointmentId }
            .map { dao ->
                ResultFileItem(
                    id = dao.id.value,
                    fileName = dao.fileName,
                    // ✅ LUÔN TRẢ VỀ ENDPOINT URL
                    // Endpoint sẽ tự động ưu tiên file đã ký nếu có
                    filePath = "/files/${dao.id.value}"
                )
            }
    }

    fun getFileById(fileId: Int): ResultFile? = transaction {
        ResultFileDAO.findById(fileId)?.toModel()
    }

    fun updateFileSignature(
        fileId: Int,
        signedFilePath: String,
        signatureHash: String,
        signedByDoctorId: Int,
        signedByDoctorName: String,
        signedAt: LocalDateTime
    ): ResultFile = transaction {
        val fileDao = ResultFileDAO.findById(fileId)
            ?: throw Exception("File không tồn tại")

        fileDao.isSigned = true
        fileDao.signedFilePath = signedFilePath
        fileDao.signatureHash = signatureHash
        fileDao.signedByDoctorId = signedByDoctorId
        fileDao.signedByDoctorName = signedByDoctorName
        fileDao.signedAt = signedAt

        fileDao.toModel()
    }
}