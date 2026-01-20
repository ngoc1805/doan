package com.example.dat_lich_kham_fe.data.api

import com.example.dat_lich_kham_fe.data.model.BaseResponse
import com.example.dat_lich_kham_fe.data.model.CanteenResponse
import com.example.dat_lich_kham_fe.data.model.PinRequest
import com.example.dat_lich_kham_fe.data.model.UpdateBalanceRequest
import com.example.dat_lich_kham_fe.data.model.UserRequest
import com.example.dat_lich_kham_fe.data.model.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface UserApi {
    @GET("/api/benhnhan/check-info")
    suspend fun check_info(@Query("accountId") accountId: Int, ): BaseResponse

    @PUT("/api/benhnhan/update")
    suspend fun update_info(@Body request: UserRequest): Response<BaseResponse>

    @GET("/api/benhnhan/info")
    suspend fun info(@Query("accountId") accountId: Int, ): Response<UserResponse>

    @PUT("/api/benhnhan/update-balance")
    suspend fun update_balance(@Body request: UpdateBalanceRequest): Response<BaseResponse>

    @GET("/api/benhnhan/has-pin")
    suspend fun has_pin(@Query("userId") userId: Int): Response<BaseResponse>

    @PUT("/api/benhnhan/update-pin")
    suspend fun update_pin(@Body request: PinRequest): Response<BaseResponse>

    @POST("/api/benhnhan/compare-pin")
    suspend fun compare_pin(@Body request: PinRequest): Response<BaseResponse>

    @GET ("api/canteen/info")
    suspend fun cabteenInfo() : Response<CanteenResponse>
}
