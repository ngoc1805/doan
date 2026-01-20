package com.example.nhom1_fe_datnhahang.data.api

import com.example.nhom1_fe_datnhahang.data.model.UserResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface UserApi {
    @GET("/api/nhaan/info")
    suspend fun info(@Query("accountId") accountId: Int, ): Response<UserResponse>
}
