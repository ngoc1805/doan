// routes/route/FileRoute.kt
package com.example.routes.route

import com.example.dto.Response.BaseResponse
import com.example.dto.Response.ListResultFileResponse
import com.example.service.ResultFileService
import com.example.service.HospitalKeyService
import com.example.utils.FileEncryptionUtil
import io.ktor.http.HttpStatusCode
import io.ktor.http.ContentType
import io.ktor.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import java.io.File
import java.security.Signature
import java.util.Base64

fun Route.FileRoute(
    service: ResultFileService,
    hospitalKeyService: HospitalKeyService
) {
    fun verifyClientSignature(fileBytes: ByteArray, signatureBase64: String): Boolean {
        try {
            val publicKey = hospitalKeyService.getPublicKey()
            val signature = Signature.getInstance("SHA256withRSA")
            signature.initVerify(publicKey)
            signature.update(fileBytes)

            val signatureBytes = Base64.getDecoder().decode(signatureBase64)
            return signature.verify(signatureBytes)
        } catch (e: Exception) {
            println(" Lỗi verify signature: ${e.message}")
            e.printStackTrace()
            return false
        }
    }

    route("/api/chucnang") {
        post("/result-files/upload") {
            val multipart = call.receiveMultipart()
            var appointmentId: Int? = null
            var doctorId: Int? = null
            var doctorName: String? = null
            var doctorTitle: String? = "Bác sĩ"
            var fileName: String? = null
            var fileType: String? = null
            var filePath: String? = null
            var clientSignature: String? = null
            var fileBytes: ByteArray? = null

            multipart.forEachPart { part ->
                when (part) {
                    is PartData.FormItem -> {
                        when (part.name) {
                            "appointmentId" -> appointmentId = part.value.toIntOrNull()
                            "doctorId" -> doctorId = part.value.toIntOrNull()
                            "doctorName" -> doctorName = part.value
                            "doctorTitle" -> doctorTitle = part.value
                            "signature" -> clientSignature = part.value
                        }
                    }
                    is PartData.FileItem -> {
                        fileName = part.originalFileName ?: "file_${System.currentTimeMillis()}"
                        fileType = part.contentType?.toString() ?: "application/octet-stream"

                        // Đọc file bytes để verify signature
                        fileBytes = part.streamProvider().readBytes()
                    }
                    else -> {}
                }
                part.dispose()
            }

            // Kiểm tra các field bắt buộc
            if (appointmentId == null || fileName == null ||
                fileType == null || clientSignature == null || fileBytes == null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    BaseResponse(
                        success = false,
                        message = "Thiếu các trường bắt buộc (bao gồm cả chữ ký số)",
                        data = null
                    )
                )
                return@post
            }

            try {
                // 1. VERIFY SIGNATURE TỪ CLIENT
                println(" Đang verify signature từ client...")
                val isSignatureValid = verifyClientSignature(fileBytes!!, clientSignature!!)

                if (!isSignatureValid) {
                    call.respond(
                        HttpStatusCode.Unauthorized,
                        BaseResponse(
                            success = false,
                            message = " Chữ ký không hợp lệ! File bị từ chối.",
                            data = null
                        )
                    )
                    return@post
                }

                println(" Signature hợp lệ! Tiếp tục xử lý...")

                // 2. MÃ HÓA VÀ LƯU FILE
                val uploadDir = File("uploads")
                if (!uploadDir.exists()) uploadDir.mkdirs()

                val encryptedFile = File(uploadDir, fileName!!)

                // Mã hóa file trước khi lưu
                println(" Đang mã hóa file: $fileName")
                FileEncryptionUtil.encryptFile(fileBytes!!, encryptedFile)

                filePath = "uploads/$fileName"
                println(" File đã được mã hóa và lưu tại: $filePath")

                // 3. LƯU THÔNG TIN FILE VÀO DATABASE
                val resultFile = service.saveFile(
                    appointmentId = appointmentId!!,
                    fileName = fileName!!,
                    filePath = filePath!!,
                    fileType = fileType!!
                )

                // 4. NẾU LÀ FILE PDF VÀ CÓ THÔNG TIN BÁC SĨ -> KÝ
                if (fileName!!.endsWith(".pdf", ignoreCase = true) &&
                    doctorId != null &&
                    doctorName != null) {

                    println(" Đang ký file PDF với visible signature: $fileName")

                    // Giải mã file tạm để ký
                    val tempDecryptedFile = File.createTempFile("decrypt_", ".pdf")
                    try {
                        FileEncryptionUtil.decryptFileToStream(encryptedFile).use { inputStream ->
                            tempDecryptedFile.outputStream().use { outputStream ->
                                inputStream.copyTo(outputStream)
                            }
                        }

                        // Ký file đã giải mã
                        val signedFile = service.signResultFile(
                            fileId = resultFile.id,
                            doctorId = doctorId!!,
                            doctorName = doctorName!!,
                            doctorTitle = doctorTitle ?: "Bác sĩ",
                            decryptedFilePath = tempDecryptedFile.absolutePath
                        )

                        println(" File đã được ký thành công!")

                        val resultJson = Json.encodeToJsonElement(
                            com.example.models.ResultFile.serializer(),
                            signedFile
                        )

                        call.respond(
                            HttpStatusCode.OK,
                            BaseResponse(
                                success = true,
                                message = " File uploaded, verified and signed successfully",
                                data = resultJson
                            )
                        )
                    } finally {
                        tempDecryptedFile.delete()
                    }
                } else {
                    // File không phải PDF hoặc không có thông tin bác sĩ
                    val resultJson = Json.encodeToJsonElement(
                        com.example.models.ResultFile.serializer(),
                        resultFile
                    )

                    call.respond(
                        HttpStatusCode.OK,
                        BaseResponse(
                            success = true,
                            message = " File uploaded and verified successfully",
                            data = resultJson
                        )
                    )
                }
            } catch (e: Exception) {
                println(" Lỗi: ${e.message}")
                e.printStackTrace()
                call.respond(
                    HttpStatusCode.InternalServerError,
                    BaseResponse(
                        success = false,
                        message = "Error: ${e.message}",
                        data = null
                    )
                )
            }
        }
    }

    route("/api/bacsi") {
        get("/result-files") {
            val appointmentId = call.request.queryParameters["appointmentId"]?.toIntOrNull()
            if (appointmentId == null) {
                call.respondText("Missing or invalid appointmentId", status = HttpStatusCode.BadRequest)
                return@get
            }

            val files = service.getFilesByAppointmentId(appointmentId)
            call.respond(
                ListResultFileResponse(resultfiles = files)
            )
        }

        get("/result-files/{fileId}/verify") {
            val fileId = call.parameters["fileId"]?.toIntOrNull()

            if (fileId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid file ID")
                return@get
            }

            try {
                val result = service.verifyFileSignature(fileId)
                call.respond(HttpStatusCode.OK, result)
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    com.example.service.SignatureVerificationResult(
                        isValid = false,
                        message = "Lỗi: ${e.message}"
                    )
                )
            }
        }

        // ENDPOINT DOWNLOAD - TỰ ĐỘNG GIẢI MÃ VÀ TRẢ VỀ FILE
        get("/result-files/{fileId}/download") {
            val fileId = call.parameters["fileId"]?.toIntOrNull()

            if (fileId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid file ID")
                return@get
            }

            val resultFileRepo = com.example.repository.ResultFileRepository()
            val file = resultFileRepo.getFileById(fileId)
            if (file == null) {
                call.respond(HttpStatusCode.NotFound, "File not found")
                return@get
            }

            // Ưu tiên file đã ký
            val filePath = if (file.isSigned && file.signedFilePath != null) {
                file.signedFilePath
            } else {
                file.filePath
            }

            val encryptedFile = File(filePath)
            if (!encryptedFile.exists()) {
                call.respond(HttpStatusCode.NotFound, "File not found on server")
                return@get
            }

            try {
                println(" Đang giải mã file: ${file.fileName}")

                call.response.header(
                    "Content-Disposition",
                    "attachment; filename=\"${file.fileName}\""
                )

                // Giải mã và stream trực tiếp cho client
                val decryptedStream = FileEncryptionUtil.decryptFileToStream(encryptedFile)
                call.respondOutputStream {
                    decryptedStream.use { input ->
                        input.copyTo(this)
                    }
                }

                println(" File đã được giải mã và gửi thành công")
            } catch (e: Exception) {
                println(" Lỗi khi giải mã file: ${e.message}")
                e.printStackTrace()
                call.respond(
                    HttpStatusCode.InternalServerError,
                    "Error decrypting file: ${e.message}"
                )
            }
        }
    }
}