package com.example.dat_lich_kham_fe.data.api

import com.example.dat_lich_kham_fe.data.model.ListResultResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ResultApi {
    @GET("api/benhnhan/results")
    suspend fun getResults(@Query("userId") userId: Int): Response<ListResultResponse>
}
