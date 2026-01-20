package com.example.dat_lich_kham_fe.viewmodel

import android.content.Context
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.dat_lich_kham_fe.data.model.DoctorResponse
import com.example.dat_lich_kham_fe.repository.DoctorRepository
import kotlinx.coroutines.launch

class DoctorViewModel(private val context: Context) : ViewModel() {
    var doctors by mutableStateOf<List<DoctorResponse>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    var currentPage by mutableStateOf(1)
        private set
    val pageSize = 20
    var hasMore by mutableStateOf(true)
        private set

    private val repository = DoctorRepository(context)

    fun fetchDoctors(departmentId: Int, isLoadMore: Boolean = false) {
        if (isLoading || !hasMore) return // ngăn gọi liên tục hoặc hết dữ liệu
        isLoading = true
        error = null
        viewModelScope.launch {
            try {
                val newDoctors = repository.getDoctorsByDepartment(departmentId, currentPage, pageSize)
                if (newDoctors.isEmpty()) {
                    hasMore = false
                } else {
                    if (isLoadMore) {
                        // Thêm vào list cũ
                        doctors = doctors + newDoctors
                    } else {
                        // Load lần đầu
                        doctors = newDoctors
                    }
                    currentPage += 1
                }
            } catch (e: Exception) {
                error = e.message
            } finally {
                isLoading = false
            }
        }
    }

    fun resetPaging() {
        doctors = emptyList()
        currentPage = 1
        hasMore = true
        error = null
        isLoading = false
    }
}
class DoctorViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DoctorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            val repo = com.example.dat_lich_kham_fe.repository.DoctorRepository(context)
            return DoctorViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
