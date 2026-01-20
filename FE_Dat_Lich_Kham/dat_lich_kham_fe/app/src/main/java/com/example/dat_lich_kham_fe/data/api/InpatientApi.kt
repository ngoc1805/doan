package com.example.dat_lich_kham_fe.data.api

import com.example.dat_lich_kham_fe.data.model.BaseResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface InpatientApi {
    @GET("api/benhnhan/inpatients/check-admitted")
    suspend fun checkAdmitted(@Query("userId") userId: Int):Response<Boolean>

    @GET("api/benhnhan/inpatients/address")
    suspend fun getAddress(@Query("userId") userId: Int):Response<ResponseBody>
}
