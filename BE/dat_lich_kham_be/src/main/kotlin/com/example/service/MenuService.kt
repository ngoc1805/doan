package com.example.service

import com.example.models.Menu
import com.example.repository.MenuRepository

class MenuService(private val repo: MenuRepository = MenuRepository()) {
    fun createMenu(name: String, description: String, examPrice: Int, category: String, imageUrl: String): Menu {
        return repo.createMenu(name, description, examPrice, category, imageUrl)
    }

    fun getAllMenus(): List<Menu> {
        return repo.getAllMenus()
    }
    fun getMenusByIsDisplay(isDisplay: Boolean): List<Menu> {
        return repo.getMenusByIsDisplay(isDisplay)
    }
    fun updateIsDisplay(id: Int, isDisplay: Boolean): Boolean {
        return repo.updateIsDisplay(id, isDisplay)
    }
}