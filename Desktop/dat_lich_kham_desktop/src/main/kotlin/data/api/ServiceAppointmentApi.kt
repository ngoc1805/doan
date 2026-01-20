package data.api

import data.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import java.time.LocalDate

interface ServiceAppointmentApi {
    @POST("api/bacsi/service-appointments")
    suspend fun createServiceAppointment(
        @Body request: ServiceAppointmentRequest
    ): Response<BaseResponse>

    @GET("api/bacsi/service-room-ids")
    suspend fun getListServiceRoomId(@Query("appointmentId") appointmentId: Int) : Response<List<Int>>

    @GET("api/chucnang/service-appointments-by-room")
    suspend fun listServiceAppointment(
        @Query("serviceRoomId") serviceRoomId: Int,
        @Query("status") status: String,
        @Query("appointmentStatus") appointmentStatus: String?,
        @Query("examDate") examDate: LocalDate?
    ) : Response<ListServiceAppointment>

    @POST("api/chucnang/service-appointments/update-status")
    suspend fun updateStatusAppointService(@Body request: UpdateServiceAppointmentStatusRequest) : Response<BaseResponse>
}