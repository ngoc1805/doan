package data.repository

import data.api.RetrofitInstance
import data.model.BaseResponse
import data.model.ResultRequest
import retrofit2.Response

class ResultRepository {
    val resultApi = RetrofitInstance.resultApi

    suspend fun createResult(appointmentId: Int, comment: String) : Response<BaseResponse> {
        val request = ResultRequest(appointmentId, comment)
        return resultApi.createResult(request)
    }
}