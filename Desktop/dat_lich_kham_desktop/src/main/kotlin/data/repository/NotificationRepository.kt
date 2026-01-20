package data.repository

import data.api.RetrofitInstance
import data.model.BaseResponse
import data.model.NotificationRequest
import retrofit2.Response

class NotificationRepository {
    val notificationApi = RetrofitInstance.notificationApi

    suspend fun postNotification(userId: Int, content: String, path: String): Response<BaseResponse> {
        val request = NotificationRequest(userId, content, path)
        return notificationApi.postNotification(request)
    }
}