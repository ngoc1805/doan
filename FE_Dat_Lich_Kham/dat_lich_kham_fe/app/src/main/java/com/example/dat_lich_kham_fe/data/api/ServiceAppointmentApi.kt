package com.example.dat_lich_kham_fe.data.api

import com.example.dat_lich_kham_fe.data.model.ListServiceItemResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Query

interface ServiceAppointmentApi {
    @GET("/api/benhnhan/service-rooms")
    suspend fun getServicrAppointment(
        @Query("appointmentId") appointmentId: Int
    ) : Response<ListServiceItemResponse>
}
