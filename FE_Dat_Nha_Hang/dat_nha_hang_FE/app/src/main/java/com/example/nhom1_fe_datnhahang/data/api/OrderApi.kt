package com.example.nhom1_fe_datnhahang.data.api

import com.example.nhom1_fe_datnhahang.data.model.OrderWithItemsResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Query

interface OrderApi {
    @GET("/api/nhaan/orders")
    suspend fun getOrders(
        @Query("status") status: List<String?>
    ): List<OrderWithItemsResponse>

    @Multipart
    @PUT("/api/nhaan/orders/update-status-and-image")
    suspend fun updateOrderStatusAndImage(
        @Part("orderId") orderId: RequestBody,
        @Part image: MultipartBody.Part
    ): Response<Unit>
}
