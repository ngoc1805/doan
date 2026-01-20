package data.api

import data.model.DoctorListResponse
import data.model.InFoDoctorResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface DoctorApi {
    @GET("/api/bacsi/doctor")
    suspend fun getDoctorByAccountId(@Query("accountId") accountId: Int): Response<InFoDoctorResponse>

    @GET("/api/admin/doctors")
    suspend fun getAllDoctors(): Response<DoctorListResponse>
}