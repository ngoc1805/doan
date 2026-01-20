package com.example.nhom1_fe_datnhahang.data.api

import com.example.nhom1_fe_datnhahang.data.model.LoginRequest
import com.example.nhom1_fe_datnhahang.data.model.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginApi {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
}
