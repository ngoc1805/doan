package com.example.Tables

import org.jetbrains.exposed.dao.id.IntIdTable

object categories : IntIdTable("categories") {
    val name = varchar("name", 100)
}