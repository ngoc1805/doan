package com.example.Tables

import com.example.Tables.Roles.default
import com.example.Tables.Roles.nullable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant

object Departments : IntIdTable("departments") {
    val name = varchar("name", 100).uniqueIndex()
    val description = text("description")
    val createdAt = timestamp("created_at").default(Instant.now())
}