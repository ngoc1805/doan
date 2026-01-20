package com.example.dat_lich_kham_fe.data.api

import com.example.dat_lich_kham_fe.data.model.RegisterRequest
import com.example.dat_lich_kham_fe.data.model.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface RegisterApi {
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>
}
