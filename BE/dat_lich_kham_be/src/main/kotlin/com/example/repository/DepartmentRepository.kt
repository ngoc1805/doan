package com.example.repository

import com.example.Tables.Departments
import com.example.dao.DepartmentDAO
import com.example.dto.Response.DepartmentResponse
import org.jetbrains.exposed.sql.transactions.transaction

class DepartmentRepository {
    fun getAllDepartments(): List<DepartmentResponse> = transaction {
        DepartmentDAO.all().map { dao ->
            DepartmentResponse(
                id = dao.id.value,
                name = dao.name,
                description = dao.description
            )
        }
    }

    fun createDepartment(name: String, description: String): Boolean = transaction {
        val existed = DepartmentDAO.find { Departments.name eq name }.firstOrNull()
        if (existed != null) return@transaction false
        DepartmentDAO.new {
            this.name = name
            this.description = description
        }
        true
    }
}