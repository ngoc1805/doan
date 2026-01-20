package com.example.Tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.datetime

object ResultFiles : IntIdTable("result_files") {
    val appointmentId = reference("appointment_id", Appointments, onDelete = ReferenceOption.CASCADE)
    val fileName = varchar("file_name", 255)
    val filePath = varchar("file_path", 255)
    val fileType = varchar("file_type", 50)

    // Thêm các field cho chữ ký số
    val isSigned = bool("is_signed").default(false)
    val signedFilePath = varchar("signed_file_path", 255).nullable()
    val signatureHash = varchar("signature_hash", 255).nullable()
    val signedByDoctorId = integer("signed_by_doctor_id").references(Doctors.id).nullable()
    val signedByDoctorName = varchar("signed_by_doctor_name", 255).nullable()
    val signedAt = datetime("signed_at").nullable()
}