package com.example.nhom1_fe_datnhahang.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nhom1_fe_datnhahang.data.api.MenuApi
import com.example.nhom1_fe_datnhahang.data.repository.MenuRepository
import com.example.nhom1_fe_datnhahang.data.model.BaseResponse
import com.example.nhom1_fe_datnhahang.data.model.Menu
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

class MenuViewModel(private val context: Context) : ViewModel() {
    private val menuRepository = MenuRepository(context)

    private val _createMenuResult = MutableStateFlow<BaseResponse?>(null)
    val createMenuResult: StateFlow<BaseResponse?> get() = _createMenuResult

    var menus by mutableStateOf<List<Menu>>(emptyList())

    fun createMenu(
        imageFile: File,
        name: String,
        description: String,
        examPrice: Int,
        category: String
    ) {
        viewModelScope.launch {
            try {
                val response = menuRepository.createMenu(imageFile, name, description, examPrice, category)
                _createMenuResult.value = response
            } catch (e: Exception) {
                _createMenuResult.value = BaseResponse(false, e.localizedMessage ?: "Lỗi không xác định", null)
            }
        }
    }

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

    fun updateMenuDisplay(id : Int, isDisplay: Boolean){
        viewModelScope.launch {
            try {
                val response = menuRepository.updateMenuDisplay(id, isDisplay)
                if(response.isSuccessful){

                }else{

                }
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }
}
