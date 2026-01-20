package com.example.dat_lich_kham_fe.data.repository

import android.content.Context
import com.example.dat_lich_kham_fe.data.api.OrderApi
import com.example.dat_lich_kham_fe.data.api.OrderItemApi
import com.example.dat_lich_kham_fe.data.api.RetrofitInstance
import com.example.dat_lich_kham_fe.data.model.BaseResponse
import com.example.dat_lich_kham_fe.data.model.OrderItemRequest
import retrofit2.Response

class OrderItemRepository(private val context: Context) {
    private val orderItemApi : OrderItemApi by lazy {
        RetrofitInstance.orderItemApi(context)
    }

    suspend fun createOrderItem(orderId: Int, menuId: Int, quantity: Int): Response<BaseResponse> {
        val request = OrderItemRequest(
            orderId = orderId,
            menuId = menuId,
            quantity = quantity
        )
        return orderItemApi.createOrderItem(request)
    }

}
