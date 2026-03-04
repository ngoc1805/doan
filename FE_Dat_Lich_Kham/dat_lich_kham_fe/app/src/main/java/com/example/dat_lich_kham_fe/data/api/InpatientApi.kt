package com.example.dat_lich_kham_fe.data.api

import com.example.dat_lich_kham_fe.data.model.BaseResponse
import com.example.dat_lich_kham_fe.data.model.InpatientItem
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface InpatientApi {
    @GET("api/benhnhan/inpatients/check-admitted")
    suspend fun checkAdmitted(@Query("userId") userId: Int):Response<Boolean>

    @GET("api/benhnhan/inpatients/address")
    suspend fun getAddress(@Query("userId") userId: Int):Response<ResponseBody>

    @GET("api/benhnhan/inpatients/current")
    suspend fun getCurrentInpatient(@Query("userId") userId: Int):Response<InpatientItem>
    @GET("inpatient/history/{userId}")
    suspend fun getInpatientHistory(@Path("userId") userId: Int): Response<List<InpatientItem>>
}
