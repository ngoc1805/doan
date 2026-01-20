package com.example.dao

import com.example.Tables.ResultFiles
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.time.format.DateTimeFormatter

class ResultFileDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ResultFileDAO>(ResultFiles)

    var appointmentId by ResultFiles.appointmentId
    var fileName by ResultFiles.fileName
    var filePath by ResultFiles.filePath
    var fileType by ResultFiles.fileType

    // Thêm các field cho chữ ký số
    var isSigned by ResultFiles.isSigned
    var signedFilePath by ResultFiles.signedFilePath
    var signatureHash by ResultFiles.signatureHash
    var signedByDoctorId by ResultFiles.signedByDoctorId
    var signedByDoctorName by ResultFiles.signedByDoctorName
    var signedAt by ResultFiles.signedAt

    fun toModel(): com.example.models.ResultFile {
        return com.example.models.ResultFile(
            id = id.value,
            appointmentId = appointmentId.value,
            fileName = fileName,
            filePath = filePath,
            fileType = fileType,
            isSigned = isSigned,
            signedFilePath = signedFilePath,
            signatureHash = signatureHash,
            signedByDoctorId = signedByDoctorId,
            signedByDoctorName = signedByDoctorName,
            signedAt = signedAt?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
        )
    }
}