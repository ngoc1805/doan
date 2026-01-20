package com.example.dao

import com.example.Tables.Doctors
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class DoctorDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<DoctorDAO>(Doctors)

    var name by Doctors.name
    var code by Doctors.code
    var accountId by Doctors.accountId
    var departmentId by Doctors.departmentId
    var examPrice by Doctors.examPrice
    var balance by Doctors.balance

    // Hàm chuyển sang model Doctor
    fun toModel(): com.example.models.Doctor {
        return com.example.models.Doctor(
            id = id.value,
            name = name,
            code = code,
            accountId = accountId.value,
            departmentId = departmentId.value,
            examPrice = examPrice,
            balance = balance
        )
    }
}