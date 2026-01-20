package com.example.dat_lich_kham_fe.data.api

import com.example.dat_lich_kham_fe.data.model.LoginRequest
import com.example.dat_lich_kham_fe.data.model.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginApi {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
}
