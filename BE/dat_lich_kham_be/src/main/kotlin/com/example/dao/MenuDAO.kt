package com.example.dao

import com.example.Tables.Menus
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class MenuDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<MenuDAO>(Menus)

    var name by Menus.name
    var examPrice by Menus.examPrice
    var description by Menus.description
    var category by Menus.category
    var isDisplay by Menus.isDisplay
    var imageUrl by Menus.imageUrl

    fun toModel(): com.example.models.Menu {
        return com.example.models.Menu(
            id = id.value,
            name = name,
            examPrice = examPrice,
            description = description,
            category = category,
            isDisplay = isDisplay,
            imageUrl = imageUrl
        )
    }
}