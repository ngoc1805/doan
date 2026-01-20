package data.api

import data.model.BaseResponse
import data.model.ResultRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ResultApi {
    @POST("api/bacsi/results")
    suspend fun createResult(@Body request: ResultRequest) : Response<BaseResponse>
}