package com.example.dat_lich_kham_fe.data.api

import com.example.dat_lich_kham_fe.data.model.DoctorListResponse
import com.example.dat_lich_kham_fe.data.model.DoctorResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface DoctorApi {
    @GET("/api/benhnhan/doctor")
    suspend fun getDoctorsByDepartment(
        @Query("departmentId") departmentId: Int,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): DoctorListResponse
}
