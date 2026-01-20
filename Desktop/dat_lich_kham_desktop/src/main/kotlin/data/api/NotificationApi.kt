package data.api

import data.model.BaseResponse
import data.model.NotificationRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface NotificationApi {
    @POST("api/notifications")
    suspend fun postNotification(@Body request: NotificationRequest): Response<BaseResponse>
}