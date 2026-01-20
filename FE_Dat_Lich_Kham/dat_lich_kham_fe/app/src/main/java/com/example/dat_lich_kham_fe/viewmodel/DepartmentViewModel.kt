package com.example.dat_lich_kham_fe.viewmodel

import android.content.Context
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.dat_lich_kham_fe.cache.DepartmentCache
import com.example.dat_lich_kham_fe.data.api.RetrofitInstance
import com.example.dat_lich_kham_fe.data.model.DepartmentResponse
import kotlinx.coroutines.launch

class DepartmentViewModel(context: Context) : ViewModel() {
    var departments by mutableStateOf<List<DepartmentResponse>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    private val api = RetrofitInstance.derpartmentApi(context)

    fun fetchDepartments() {
        // Nếu đã có cache thì dùng luôn
        DepartmentCache.departments?.let {
            departments = it
            return
        }
        isLoading = true
        error = null
        viewModelScope.launch {
            try {
                val result = api.departments()
                departments = result
                DepartmentCache.departments = result // lưu cache
            } catch (e: Exception) {
                error = "Không lấy được danh sách khoa"
            } finally {
                isLoading = false
            }
        }
    }
}

class DepartmentViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DepartmentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DepartmentViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
