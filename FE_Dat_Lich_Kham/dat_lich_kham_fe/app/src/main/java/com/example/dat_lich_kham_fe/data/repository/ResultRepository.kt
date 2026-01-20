package com.example.dat_lich_kham_fe.data.repository

import android.content.Context
import com.example.dat_lich_kham_fe.data.api.ResultApi
import com.example.dat_lich_kham_fe.data.api.RetrofitInstance
import com.example.dat_lich_kham_fe.data.model.ListResultResponse
import retrofit2.Response

class ResultRepository(private val context: Context) {
    private val resultApi: ResultApi by lazy {
        RetrofitInstance.resultApi(context)
    }

    suspend fun getResults(userId: Int): Response<ListResultResponse> {
        return resultApi.getResults(userId)
    }
}
