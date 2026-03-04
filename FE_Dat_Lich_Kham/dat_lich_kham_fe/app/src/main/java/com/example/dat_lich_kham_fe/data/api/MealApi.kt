package com.example.dat_lich_kham_fe.data.api

import com.example.dat_lich_kham_fe.data.model.BaseResponse
import com.example.dat_lich_kham_fe.data.model.MealCycleListResponse
import com.example.dat_lich_kham_fe.data.model.MealSkipRequest
import com.example.dat_lich_kham_fe.data.model.MealStatisticsResponse
import com.example.dat_lich_kham_fe.data.model.MealStatusResponse
import com.example.dat_lich_kham_fe.data.model.MealSubscriptionRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface MealApi {
    @POST("api/benhnhan/meals/register")
    suspend fun registerMeal(@Body request: MealSubscriptionRequest): Response<BaseResponse>

    @POST("api/benhnhan/meals/renew")
    suspend fun renewMeal(@Query("inpatientId") inpatientId: Int): Response<BaseResponse>

    @POST("api/benhnhan/meals/skip")
    suspend fun skipMeal(@Body request: MealSkipRequest): Response<BaseResponse>

    @GET("api/benhnhan/meals/status")
    suspend fun getMealStatus(@Query("inpatientId") inpatientId: Int): Response<MealStatusResponse>

    @GET("api/benhnhan/meals/history")
    suspend fun getMealHistory(@Query("inpatientId") inpatientId: Int): Response<MealCycleListResponse>

    @GET("api/benhnhan/meals/statistics")
    suspend fun getMealStatistics(@Query("inpatientId") inpatientId: Int): Response<MealStatisticsResponse>
}
