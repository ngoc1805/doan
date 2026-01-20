package com.example.nhom1_fe_datnhahang.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nhom1_fe_datnhahang.data.model.OrderWithItemsResponse
import com.example.nhom1_fe_datnhahang.data.repository.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OrderViewModel(private val context: Context) : ViewModel() {
    private val orderRepository = OrderRepository(context)

    private val _orders = MutableStateFlow<List<OrderWithItemsResponse>?>(null)
    val orders: StateFlow<List<OrderWithItemsResponse>?> = _orders

    private val _uploadStatus = MutableStateFlow<UploadStatus>(UploadStatus.Idle)
    val uploadStatus: StateFlow<UploadStatus> = _uploadStatus

    fun fetchOrders(status: List<String>){
        viewModelScope.launch {
            try {
                _orders.value = orderRepository.getOrders(status)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateOrderStatusAndImage(orderId: Int, imageUri: Uri) {
        viewModelScope.launch {
            try {
                _uploadStatus.value = UploadStatus.Loading
                val success = orderRepository.updateOrderStatusAndImage(orderId, imageUri)

                if (success) {
                    _uploadStatus.value = UploadStatus.Success
                } else {
                    _uploadStatus.value = UploadStatus.Error("Cập nhật đơn hàng thất bại")
                }
            } catch (e: Exception) {
                _uploadStatus.value = UploadStatus.Error(e.message ?: "Lỗi không xác định")
            }
        }
    }

    fun resetUploadStatus() {
        _uploadStatus.value = UploadStatus.Idle
    }
}

sealed class UploadStatus {
    object Idle : UploadStatus()
    object Loading : UploadStatus()
    object Success : UploadStatus()
    data class Error(val message: String) : UploadStatus()
}
