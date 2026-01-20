package com.example.dat_lich_kham_fe.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dat_lich_kham_fe.data.model.OrderWithItemsResponse
import com.example.dat_lich_kham_fe.data.repository.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OrderViewModel(private val context: Context): ViewModel() {
    private val orderRepository = OrderRepository(context)

    private val _orders = MutableStateFlow<List<OrderWithItemsResponse>?>(null)
    val orders: StateFlow<List<OrderWithItemsResponse>?> = _orders

    suspend fun createOrder(
        userId: Int,
        phone: String,
        address: String,
        note: String,
        status: String
    ): Int? {
        val response = orderRepository.createOrder(userId, phone, address, note, status)
        if (response.isSuccessful) {
            val baseResponse = response.body()
            // Giả sử data là một object có field "id"
            val id = baseResponse?.data?.asJsonObject?.get("id")?.asInt
            return id
        }
        return null
    }

    fun fetchOrders(userId: Int, status: List<String?>){
        viewModelScope.launch {
            try {
                _orders.value = orderRepository.getOrders(userId, status)
            }catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
