package com.example.dat_lich_kham_fe.data.api

import com.example.dat_lich_kham_fe.data.model.AppointmentByUserIdListResponse
import com.example.dat_lich_kham_fe.data.model.AppointmentItem
import com.example.dat_lich_kham_fe.data.model.AppointmentRequest
import com.example.dat_lich_kham_fe.data.model.BaseResponse
import com.example.dat_lich_kham_fe.data.model.FreeTimeRequest
import com.example.dat_lich_kham_fe.data.model.FreeTimeResponse
import com.example.dat_lich_kham_fe.data.model.UpdateAppointmentSatatusRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AppointmentApi {
    @POST("api/benhnhan/free-time")
    suspend fun freetime(@Body request: FreeTimeRequest): Response<FreeTimeResponse>

    @POST("api/benhnhan/appointment")
    suspend fun bookAppointment(@Body request: AppointmentRequest): Response<BaseResponse>

    @GET("api/benhnhan/appointments")
    suspend fun listAppointments(
        @Query("userId") userId: Int,
        @Query("status") status: List<String?>
    ): Response<AppointmentByUserIdListResponse>

    @POST("api/benhnhan/update-appointment-status")
    suspend fun updateAppointmentStatus(
        @Body request: UpdateAppointmentSatatusRequest
    ): Response<BaseResponse>

    @GET("api/benhnhan//nearest-appointment")
    suspend fun nearestAppointment(
        @Query("userId") userId: Int
    ): Response<AppointmentItem>
}
