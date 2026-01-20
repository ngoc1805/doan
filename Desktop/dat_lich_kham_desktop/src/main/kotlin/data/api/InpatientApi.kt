package data.api

import data.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface InpatientApi {
    @POST("api/bacsi/inpatients")
    suspend fun createInpatient(@Body request: InpatientRequest) : Response<BaseResponse>

    @GET("api/admin/inpatients/by-status")
    suspend fun getInpatient(@Query("status") status: String) : Response<InpatientListResponse>

    @POST("api/admin/inpatients/discharge")
    suspend fun updateStatus(@Body request: UpdateStatusInpatient) : Response<BaseResponse>

    @POST("api/admin/inpatients/update-address")
    suspend fun updateAddress(@Body request: UpdateAddressRequest) : Response<BaseResponse>
}