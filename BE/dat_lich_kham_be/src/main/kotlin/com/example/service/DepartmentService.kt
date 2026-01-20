package com.example.service

import com.example.dto.Response.DepartmentResponse
import com.example.repository.DepartmentRepository

class DepartmentService(private val departmentRepository: DepartmentRepository) {
    fun getAllDepartments(): List<DepartmentResponse> {
        return departmentRepository.getAllDepartments()
    }

    fun createDepartment(name: String, description: String): Boolean {
        return departmentRepository.createDepartment(name, description)
    }
}