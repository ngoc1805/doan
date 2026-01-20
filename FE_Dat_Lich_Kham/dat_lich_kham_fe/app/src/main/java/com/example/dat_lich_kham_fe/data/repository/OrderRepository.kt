package com.example.dat_lich_kham_fe.data.repository

import android.content.Context
import com.example.dat_lich_kham_fe.data.api.OrderApi
import com.example.dat_lich_kham_fe.data.api.RetrofitInstance
import com.example.dat_lich_kham_fe.data.model.BaseResponse
import com.example.dat_lich_kham_fe.data.model.OrderRequest
import com.example.dat_lich_kham_fe.data.model.OrderWithItemsResponse
import okhttp3.Address
import retrofit2.Response

class OrderRepository(private val context: Context) {
    private val orderApi: OrderApi by lazy {
        RetrofitInstance.orderApi(context)
    }

    suspend fun createOrder(userId: Int, phone: String,address: String, note: String, status: String): Response<BaseResponse> {
        val request = OrderRequest(
            userId = userId,
            phone = phone,
            address = address,
            note = note,
            status = status
        )
        return orderApi.createOrder(request)
    }

    suspend fun getOrders(userId: Int, status: List<String?>): List<OrderWithItemsResponse>? {
        return try {
            orderApi.getOrders(userId, status)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

}
