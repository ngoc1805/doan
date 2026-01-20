package com.example.dat_lich_kham_fe.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dat_lich_kham_fe.data.model.ListResultResponse
import com.example.dat_lich_kham_fe.data.model.ResultItem
import com.example.dat_lich_kham_fe.data.repository.ResultRepository
import kotlinx.coroutines.launch

class ResultViewModel(private val context: Context) : ViewModel() {
    private val repository = ResultRepository(context)
    var results by mutableStateOf<List<ResultItem>>(emptyList())
        private set
    var isLoading by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    fun fetchResults(userId: Int) {
        isLoading = true
        error = null
        viewModelScope.launch {
            try {
                val response = repository.getResults(userId)
                if (response.isSuccessful) {
                    results = response.body()?.results ?: emptyList()
                } else {
                    error = "Không thể lấy kết quả"
                }
            } catch (e: Exception) {
                error = e.message
            } finally {
                isLoading = false
            }
        }
    }
}
