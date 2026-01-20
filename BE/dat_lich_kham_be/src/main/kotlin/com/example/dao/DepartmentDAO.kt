package com.example.dao

import com.example.Tables.Departments
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class DepartmentDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<DepartmentDAO>(Departments)

    var name by Departments.name
    var description by Departments.description
    var createdAt by Departments.createdAt

    fun toModel(): com.example.models.Department {
        return com.example.models.Department(
            id = id.value,
            name = name,
            description = description,
        )
    }
}