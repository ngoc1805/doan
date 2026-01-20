package com.example.nhom1_fe_datnhahang.data.api


import com.example.nhom1_fe_datnhahang.data.model.BaseResponse
import com.example.nhom1_fe_datnhahang.data.model.NotificationListResponse
import com.example.nhom1_fe_datnhahang.data.model.NotificationRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface NotificationApi {
    @POST("api/notifications")
    suspend fun postNotification(@Body request: NotificationRequest): Response<BaseResponse>

    @GET("api/nhaan/list-notification")
    suspend fun getNotification(
        @Query("userId") userId: Int,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<NotificationListResponse>

    @POST("api/nhaan/mark-seen")
    suspend fun markNotificationSeen(
        @Query("id") id: Int
    ): Response<BaseResponse>

    @GET("api/nhaan/all-received")
    suspend fun checkAllReceived(@Query("userId") userId: Int): Response<BaseResponse>

    @POST("api/nhaan/mark-all-received")
    suspend fun markAllReceived(@Query("userId") userId: Int): Response<BaseResponse>
}
