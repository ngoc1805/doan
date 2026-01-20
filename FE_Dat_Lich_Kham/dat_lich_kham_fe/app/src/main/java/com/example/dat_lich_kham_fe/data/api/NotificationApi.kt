package com.example.dat_lich_kham_fe.data.api

import com.example.dat_lich_kham_fe.data.model.BaseResponse
import com.example.dat_lich_kham_fe.data.model.NotificationListResponse
import com.example.dat_lich_kham_fe.data.model.NotificationRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface NotificationApi {
    @POST("api/notifications")
    suspend fun postNotification(@Body request: NotificationRequest): Response<BaseResponse>

    @GET("api/benhnhan/list-notification")
    suspend fun getNotification(
        @Query("userId") userId: Int,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<NotificationListResponse>

    @POST("api/benhnhan/mark-seen")
    suspend fun markNotificationSeen(
        @Query("id") id: Int
    ): Response<BaseResponse>

    @GET("api/benhnhan/all-received")
    suspend fun checkAllReceived(@Query("userId") userId: Int): Response<BaseResponse>

    @POST("api/benhnhan/mark-all-received")
    suspend fun markAllReceived(@Query("userId") userId: Int): Response<BaseResponse>
}
