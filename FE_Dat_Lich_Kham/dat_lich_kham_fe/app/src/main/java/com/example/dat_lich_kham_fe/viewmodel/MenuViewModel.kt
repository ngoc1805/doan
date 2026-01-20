package com.example.dat_lich_kham_fe.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dat_lich_kham_fe.data.model.Menu
import com.example.dat_lich_kham_fe.data.repository.MenuRepository
import kotlinx.coroutines.launch

class MenuViewModel(private val context: Context) : ViewModel() {
    private val menuRepository = MenuRepository(context)
    var menus by mutableStateOf<List<Menu>>(emptyList())

    fun fetchMenus(isDisplay: Boolean?) {
        viewModelScope.launch {
            try {
                val response = menuRepository.getAllMenu(isDisplay)
                if (response.isSuccessful){
                    menus = response.body()?.menus ?: emptyList()
                }else{

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
