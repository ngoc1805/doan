// routes/route/PublicFileRoute.kt
package com.example.routes.route

import com.example.repository.ResultFileRepository
import com.example.utils.FileEncryptionUtil
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

/**
 * Route PUBLIC để serve file đã mã hóa
 * Route này KHÔNG CẦN AUTHENTICATION
 * Phù hợp để FE truy cập trực tiếp file qua URL
 */
fun Route.publicFileRoute() {
    route("/api/files") {
        get("/{fileId}") {
            val fileId = call.parameters["fileId"]?.toIntOrNull()

            if (fileId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid file ID")
                return@get
            }

            val resultFileRepo = ResultFileRepository()
            val file = resultFileRepo.getFileById(fileId)
            if (file == null) {
                call.respond(HttpStatusCode.NotFound, "File not found")
                return@get
            }

            // Ưu tiên file đã ký (có chữ ký visible)
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
                println(" Đang serve file: ${file.fileName} (ID: $fileId)")

                // Xác định content type dựa vào extension
                val contentType = when {
                    file.fileName.endsWith(".pdf", ignoreCase = true) -> ContentType.Application.Pdf
                    file.fileName.endsWith(".jpg", ignoreCase = true) ||
                            file.fileName.endsWith(".jpeg", ignoreCase = true) -> ContentType.Image.JPEG
                    file.fileName.endsWith(".png", ignoreCase = true) -> ContentType.Image.PNG
                    else -> ContentType.Application.OctetStream
                }

                call.response.header(
                    "Content-Disposition",
                    "inline; filename=\"${file.fileName}\""
                )

                // Giải mã và stream trực tiếp
                val decryptedStream = FileEncryptionUtil.decryptFileToStream(encryptedFile)
                call.respondOutputStream(contentType = contentType) {
                    decryptedStream.use { input ->
                        input.copyTo(this)
                    }
                }

                println(" File đã được serve thành công: ${file.fileName}")
            } catch (e: Exception) {
                println(" Lỗi khi serve file: ${e.message}")
                e.printStackTrace()
                call.respond(
                    HttpStatusCode.InternalServerError,
                    "Error serving file: ${e.message}"
                )
            }
        }
    }
}