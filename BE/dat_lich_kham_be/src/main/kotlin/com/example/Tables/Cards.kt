package com.example.Tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object Cards : IntIdTable("cards") {
    val userId = reference("user_id", Users, onDelete = ReferenceOption.CASCADE)

}