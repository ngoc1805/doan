package com.example.dat_lich_kham_fe.data.repository

import com.example.dat_lich_kham_fe.data.api.MealApi
import com.example.dat_lich_kham_fe.data.model.BaseResponse
import com.example.dat_lich_kham_fe.data.model.MealCycleListResponse
import com.example.dat_lich_kham_fe.data.model.MealSkipRequest
import com.example.dat_lich_kham_fe.data.model.MealStatisticsResponse
import com.example.dat_lich_kham_fe.data.model.MealStatusResponse
import com.example.dat_lich_kham_fe.data.model.MealSubscriptionRequest
import retrofit2.Response

class MealRepository(private val mealApi: MealApi) {
    
    suspend fun registerMeal(inpatientId: Int): Response<BaseResponse> {
        val request = MealSubscriptionRequest(inpatientId)
        return mealApi.registerMeal(request)
    }

    suspend fun renewMeal(inpatientId: Int): Response<BaseResponse> {
        return mealApi.renewMeal(inpatientId)
    }

    suspend fun skipMeal(inpatientId: Int, skipDate: String): Response<BaseResponse> {
        val request = MealSkipRequest(inpatientId, skipDate)
        return mealApi.skipMeal(request)
    }

    suspend fun getMealStatus(inpatientId: Int): Response<MealStatusResponse> {
        return mealApi.getMealStatus(inpatientId)
    }

    suspend fun getMealHistory(inpatientId: Int): Response<MealCycleListResponse> {
        return mealApi.getMealHistory(inpatientId)
    }

    suspend fun getMealStatistics(inpatientId: Int): Response<MealStatisticsResponse> {
        return mealApi.getMealStatistics(inpatientId)
    }
}
