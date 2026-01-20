package com.example.repository

import com.example.Tables.Menus
import com.example.dao.MenuDAO
import com.example.models.Menu
import org.jetbrains.exposed.sql.transactions.transaction

class MenuRepository {
    fun createMenu(name: String, description: String, examPrice: Int, category: String, imageUrl: String): Menu = transaction {
        val menuDao = MenuDAO.new {
            this.name = name
            this.description = description
            this.examPrice = examPrice
            this.category = category
            this.isDisplay = false // hoặc true nếu mặc định muốn hiển thị
            this.imageUrl = imageUrl
        }
        menuDao.toModel()
    }
    //
    fun getAllMenus(): List<Menu> = transaction {
        MenuDAO.all().map { it.toModel() }
    }
    fun getMenusByIsDisplay(isDisplay: Boolean): List<Menu> = transaction {
        MenuDAO.find { com.example.Tables.Menus.isDisplay eq isDisplay }.map { it.toModel() }
    }
    //
    fun updateIsDisplay(id: Int, isDisplay: Boolean): Boolean = transaction {
        val menu = MenuDAO.findById(id)
        if (menu != null) {
            menu.isDisplay = isDisplay
            true
        } else {
            false
        }
    }
}