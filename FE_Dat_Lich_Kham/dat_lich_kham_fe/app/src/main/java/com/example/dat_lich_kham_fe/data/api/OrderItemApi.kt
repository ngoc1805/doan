package com.example.dat_lich_kham_fe.data.api

import com.example.dat_lich_kham_fe.data.model.BaseResponse
import com.example.dat_lich_kham_fe.data.model.OrderItemRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface OrderItemApi {
    @POST("api/benhnhan/order-items")
    suspend fun createOrderItem(
        @Body request: OrderItemRequest
    ): Response<BaseResponse>
}
