package com.example.dao

import com.example.Tables.Roles
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class RoleDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<RoleDAO>(Roles)

    var roleName by Roles.roleName
    var description by Roles.description
    var createdAt by Roles.createdAt
}