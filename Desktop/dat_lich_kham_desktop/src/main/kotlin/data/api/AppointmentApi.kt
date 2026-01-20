package data.api

import data.model.AppointmentByDoctorIdListResponse
import data.model.BaseResponse
import data.model.UpdateAppointmentSatatusRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import java.time.LocalDate

interface AppointmentApi {
    @GET("api/bacsi/appointments-by-doctor")
    suspend fun listAppointmentBydoctorId(
        @Query("doctorId") doctorId: Int,
        @Query("examDate") examDate: LocalDate?,
        @Query("status") status: List<String?>
    ): Response<AppointmentByDoctorIdListResponse>

    @POST("api/bacsi/update-appointment-status")
    suspend fun updateAppointmentStatus(
        @Body request: UpdateAppointmentSatatusRequest
    ): Response<BaseResponse>
}