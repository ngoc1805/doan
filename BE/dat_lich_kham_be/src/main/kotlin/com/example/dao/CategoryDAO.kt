package com.example.dao

import com.example.Tables.categories
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class CategoryDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<CategoryDAO>(categories)

    var name by categories.name

    fun toModel(): com.example.models.Category {
        return com.example.models.Category(
            id = id.value,
            name = name
        )
    }
}