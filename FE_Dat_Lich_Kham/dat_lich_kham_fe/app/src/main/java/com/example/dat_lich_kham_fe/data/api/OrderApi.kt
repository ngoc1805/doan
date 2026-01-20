package com.example.dat_lich_kham_fe.data.api

import com.example.dat_lich_kham_fe.data.model.BaseResponse
import com.example.dat_lich_kham_fe.data.model.OrderRequest
import com.example.dat_lich_kham_fe.data.model.OrderWithItemsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface OrderApi {
    @POST("api/benhnhan/orders")
    suspend fun createOrder(
        @Body request: OrderRequest
    ): Response<BaseResponse>

    @GET("api/benhnhan/orders/by-user")
    suspend fun getOrders(
        @Query("userId") userId: Int,
        @Query("status") status: List<String?>,
    ): List<OrderWithItemsResponse>
}
