package com.example.service

import com.example.dto.Response.InpatientItem
import com.example.dto.Response.InpatientListResponse
import com.example.repository.InpatientRepository

class InpatientService(private val repo: InpatientRepository = InpatientRepository()) {

    /**
     * Kiểm tra bệnh nhân có trạng thái cụ thể không
     */
    fun hasInpatientWithStatus(userId: Int, status: String): Boolean {
        return repo.hasInpatientWithStatus(userId, status)
    }

    /**
     * Lấy địa chỉ nội trú của bệnh nhân theo trạng thái
     */
    fun getInpatientAddress(userId: Int, status: String): String? {
        return repo.getInpatientAddress(userId, status)
    }

    /**
     * Tạo bản ghi nội trú mới
     */
    fun createInpatient(userId: Int, appointmentId: Int? = null): Int {
        return repo.createInpatient(userId, appointmentId)
    }

    /**
     * Lấy danh sách bệnh nhân nội trú theo trạng thái
     */
    fun getInpatientsByStatus(status: String): InpatientListResponse {
        return repo.getInpatientsByStatus(status)
    }

    /**
     * Xuất viện - cập nhật trạng thái
     */
    fun dischargeInpatientById(id: Int): Boolean {
        return repo.dischargeInpatientById(id)
    }

    /**
     * Cập nhật địa chỉ và đổi trạng thái sang "Đã nhập viện"
     */
    fun updateAddressAndAdmit(id: Int, address: String): Boolean {
        return repo.updateAddressAndAdmit(id, address)
    }

    /**
     * Lấy thông tin nội trú hiện tại của bệnh nhân (trạng thái "Đã nhập viện")
     */
    fun getCurrentInpatient(userId: Int): InpatientItem? {
        return repo.getCurrentInpatient(userId)
    }

    /**
     * Lấy lịch sử các lần nội trú của bệnh nhân (trạng thái "Đã xuất viện")
     * Function mới này cần thiết cho tab "Lịch sử nội trú"
     */
    fun getInpatientHistory(userId: Int): List<InpatientItem> {
        return repo.getInpatientHistory(userId)
    }

    /**
     * Lấy thông tin chi tiết một bản ghi nội trú theo ID
     * Function mới này cần thiết cho logic hoàn tiền
     */
    fun getInpatientById(id: Int): InpatientItem? {
        return repo.getInpatientById(id)
    }
}