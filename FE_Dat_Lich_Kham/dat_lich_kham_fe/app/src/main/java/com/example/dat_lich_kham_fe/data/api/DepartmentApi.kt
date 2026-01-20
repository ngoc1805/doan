package com.example.dat_lich_kham_fe.data.api

import com.example.dat_lich_kham_fe.data.model.DepartmentResponse
import retrofit2.http.GET

interface DepartmentApi {
    @GET("api/departments")
    suspend fun departments(): List<DepartmentResponse>
}
