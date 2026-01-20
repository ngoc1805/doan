package com.example.service

import com.example.dto.Response.InpatientListResponse
import com.example.repository.InpatientRepository

class InpatientService(private val repo: InpatientRepository = InpatientRepository()) {
    fun hasInpatientWithStatus(userId: Int, status: String): Boolean {
        return repo.hasInpatientWithStatus(userId, status)
    }

    fun getInpatientAddress(userId: Int, status: String): String? {
        return repo.getInpatientAddress(userId, status)
    }

    fun createInpatient(userId: Int): Int {
        return repo.createInpatient(userId)
    }

    fun getInpatientsByStatus(status: String): InpatientListResponse {
        return repo.getInpatientsByStatus(status)
    }

    fun dischargeInpatientById(id: Int): Boolean {
        return repo.dischargeInpatientById(id)
    }

    fun updateAddressAndAdmit(id: Int, address: String): Boolean {
        return repo.updateAddressAndAdmit(id, address)
    }
}