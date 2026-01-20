package com.example.repository

import com.example.dao.DoctorDAO
import com.example.dto.Response.DoctorListItem
import com.example.Tables.Departments
import com.example.Tables.Doctors
import com.example.dao.DepartmentDAO
import com.example.dto.Response.DoctorListResponse
import com.example.dto.Response.InFoDoctorResponse
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class DoctorRepository {
    fun getDoctorsByDepartmentPaged(departmentId: Int, page: Int, size: Int): List<DoctorListItem> = transaction {
        DoctorDAO.find { Doctors.departmentId eq departmentId }
            .limit(size, offset = ((page - 1) * size).toLong())
            .map { doctorDao ->
                val departmentName = Departments
                    .select { Departments.id eq doctorDao.departmentId.value }
                    .singleOrNull()?.get(Departments.name) ?: ""
                DoctorListItem(
                    id = doctorDao.id.value,
                    name = doctorDao.name,
                    code = doctorDao.code,
                    examPrice = doctorDao.examPrice,
                    department = departmentName
                )
            }
    }

    fun getDoctorByaccountId(accountId: Int): InFoDoctorResponse? = transaction {
        val doctorDao = DoctorDAO.find { Doctors.accountId eq accountId }
            .singleOrNull()
        doctorDao?.let {
            // Lấy tên khoa từ bảng Departments
            val departmentName = DepartmentDAO.findById(it.departmentId.value)?.name ?: ""
            InFoDoctorResponse(
                id = it.id.value,
                name = it.name,
                code = it.code,
                examPrice = it.examPrice,
                department = departmentName,
                balance = it.balance
            )
        }
    }

    fun getAllDoctors(): DoctorListResponse = transaction {
        val doctors = DoctorDAO.all().map { doctorDao ->
            val departmentName = Departments
                .select { Departments.id eq doctorDao.departmentId.value }
                .singleOrNull()?.get(Departments.name) ?: ""
            DoctorListItem(
                id = doctorDao.id.value,
                name = doctorDao.name,
                code = doctorDao.code,
                examPrice = doctorDao.examPrice,
                department = departmentName
            )
        }
        DoctorListResponse(doctors)
    }

}