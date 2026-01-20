package data.api

import data.model.BaseResponse
import data.model.CreateDepartmentRequest
import data.model.DepartmentResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface DepartmentApi {
    @GET("api/departments")
    suspend fun departments(): List<DepartmentResponse>

    @POST("api/departments")
    suspend fun createDepartment(
        @Body request: CreateDepartmentRequest
    ) : Response<BaseResponse>
}