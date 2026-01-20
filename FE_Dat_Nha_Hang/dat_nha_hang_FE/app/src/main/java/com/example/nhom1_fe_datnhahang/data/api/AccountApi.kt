package com.example.nhom1_fe_datnhahang.data.api

import com.example.nhom1_fe_datnhahang.data.model.BaseResponse
import com.example.nhom1_fe_datnhahang.data.model.ChangePasswordRequest
import com.example.nhom1_fe_datnhahang.data.model.UpdateFmcTokenRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AccountApi {
    @POST("/api/update-fmc-token")
        suspend fun updateFmcToken(@Body request: UpdateFmcTokenRequest): Response<BaseResponse>

    @POST("/api/change-password")
    suspend fun changePassword(
        @Body request: ChangePasswordRequest
    ): Response<BaseResponse>
}
