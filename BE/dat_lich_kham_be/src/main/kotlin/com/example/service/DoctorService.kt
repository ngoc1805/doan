package com.example.service

import com.example.dto.Response.DoctorListItem
import com.example.dto.Response.DoctorListResponse
import com.example.dto.Response.InFoDoctorResponse
import com.example.repository.DoctorRepository

class DoctorService(private val doctorRepository: DoctorRepository) {
    fun getDoctorsByDepartmentPaged(departmentId: Int, page: Int, size: Int): List<DoctorListItem> {
        return doctorRepository.getDoctorsByDepartmentPaged(departmentId, page, size)
    }

    fun getDoctorByAccountId(accountId: Int): InFoDoctorResponse? {
        return doctorRepository.getDoctorByaccountId(accountId)
    }

    fun getAllDoctors(): DoctorListResponse {
        return doctorRepository.getAllDoctors()
    }
}