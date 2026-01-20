package com.example.service

import com.example.dto.Response.ResultFileItem
import com.example.models.ResultFile
import com.example.repository.ResultFileRepository
import com.example.utils.FileEncryptionUtil
import java.io.File
import java.time.LocalDateTime

class ResultFileService(
    private val repo: ResultFileRepository,
    private val signatureService: DigitalSignatureService
) {
    fun saveFile(
        appointmentId: Int,
        fileName: String,
        filePath: String,
        fileType: String
    ): ResultFile {
        return repo.addResultFile(
            appointmentId = appointmentId,
            fileName = fileName,
            filePath = filePath,
            fileType = fileType
        )
    }

    fun getFilesByAppointmentId(appointmentId: Int): List<ResultFileItem> {
        return repo.getFilesByAppointmentId(appointmentId)
    }


    fun signResultFile(
        fileId: Int,
        doctorId: Int,
        doctorName: String,
        doctorTitle: String = "Bác sĩ",
        decryptedFilePath: String? = null
    ): ResultFile {
        // Lấy thông tin file
        val file = repo.getFileById(fileId)
            ?: throw Exception("File không tồn tại")

        // Kiểm tra file type
        if (!file.filePath.endsWith(".pdf", ignoreCase = true)) {
            throw Exception("Chỉ hỗ trợ ký file PDF")
        }

        // Kiểm tra đã ký chưa
        if (file.isSigned) {
            throw Exception("File đã được ký rồi")
        }

        val tempFilesToDelete = mutableListOf<File>()

        try {
            // 1. Giải mã file gốc (nếu chưa có file đã giải mã)
            val decryptedFile = if (decryptedFilePath != null) {
                File(decryptedFilePath)
            } else {
                val encryptedFile = File(file.filePath)
                val tempFile = File.createTempFile("decrypt_", ".pdf")
                tempFilesToDelete.add(tempFile)

                println(" Đang giải mã file để ký: ${file.fileName}")
                FileEncryptionUtil.decryptFileToStream(encryptedFile).use { inputStream ->
                    tempFile.outputStream().use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                tempFile
            }

            // 2. Ký file đã giải mã
            val tempSignedFile = File.createTempFile("signed_", ".pdf")
            tempFilesToDelete.add(tempSignedFile)

            println(" Đang ký file PDF: ${file.fileName}")
            val signatureHash = signatureService.signPdfWithVisibleSignature(
                inputPdfPath = decryptedFile.absolutePath,
                outputPdfPath = tempSignedFile.absolutePath,
                doctorName = doctorName,
                doctorTitle = doctorTitle,
                reason = "Kết quả khám bệnh"
            )

            // 3. Mã hóa file đã ký
            val originalFile = File(file.filePath)
            val signedFileName = "signed_${originalFile.name}"
            val signedPath = "uploads/signed/$signedFileName"
            val encryptedSignedFile = File(signedPath)

            // Tạo thư mục signed nếu chưa có
            File("uploads/signed").mkdirs()

            println(" Đang mã hóa file đã ký: $signedFileName")
            FileEncryptionUtil.encryptFile(tempSignedFile, encryptedSignedFile)
            println(" File đã ký đã được mã hóa và lưu tại: $signedPath")

            // 4. Cập nhật database
            return repo.updateFileSignature(
                fileId = fileId,
                signedFilePath = signedPath,
                signatureHash = signatureHash,
                signedByDoctorId = doctorId,
                signedByDoctorName = doctorName,
                signedAt = LocalDateTime.now()
            )
        } finally {
            // Xóa các file tạm
            tempFilesToDelete.forEach { it.delete() }
        }
    }


    fun verifyFileSignature(fileId: Int): SignatureVerificationResult {
        val file = repo.getFileById(fileId)
            ?: throw Exception("File không tồn tại")

        if (!file.isSigned || file.signedFilePath == null) {
            return SignatureVerificationResult(
                isValid = false,
                message = "File chưa được ký"
            )
        }

        // Giải mã file đã ký tạm để verify
        val encryptedSignedFile = File(file.signedFilePath)
        val tempFile = File.createTempFile("verify_", ".pdf")

        try {
            println(" Đang giải mã file để verify: ${file.fileName}")
            FileEncryptionUtil.decryptFileToStream(encryptedSignedFile).use { inputStream ->
                tempFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            return signatureService.verifyPdfSignature(tempFile.absolutePath)
        } finally {
            tempFile.delete()
        }
    }
}