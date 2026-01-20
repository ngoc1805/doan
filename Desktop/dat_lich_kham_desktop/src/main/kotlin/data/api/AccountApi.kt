package data.api

import data.model.BaseResponse
import data.model.ChangePasswordRequest
import data.model.CreateDoctorRequest
import data.model.CreateServiceRoomRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AccountApi {
    @POST("/api/doctor/create")
    suspend fun createDoctor(
        @Body request: CreateDoctorRequest
    ): Response<BaseResponse>

    @POST("/api/change-password")
    suspend fun changePassword(
        @Body request: ChangePasswordRequest
    ): Response<BaseResponse>

    @POST("/api/service-room/create")
    suspend fun createServiceRoom(
        @Body request: CreateServiceRoomRequest
    ): Response<BaseResponse>
}