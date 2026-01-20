package com.example.Tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant

object Roles : IntIdTable("roles") {
    //val roleId = integer("role_id").autoIncrement()
    val roleName = varchar("role_name", 50)
    val description = text("description").nullable()
    val createdAt = timestamp("created_at").default(Instant.now())

//    override val primaryKey = PrimaryKey(roleId)
}