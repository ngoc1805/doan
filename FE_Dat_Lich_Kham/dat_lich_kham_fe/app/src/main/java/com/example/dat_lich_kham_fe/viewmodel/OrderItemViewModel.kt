package com.example.dat_lich_kham_fe.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dat_lich_kham_fe.data.repository.OrderItemRepository
import kotlinx.coroutines.launch

class OrderItemViewModel(private val context: Context) : ViewModel() {
    private val orderItemRepository = OrderItemRepository(context)

    fun createOrderItem(orderId: Int, menuId: Int, quantity: Int) {
        viewModelScope.launch {
            try {
                val response = orderItemRepository.createOrderItem(orderId, menuId, quantity)
                if(response.isSuccessful){

                }else{
                    println("Error: ${response.code()} - ${response.message()}")
                }
            }catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
