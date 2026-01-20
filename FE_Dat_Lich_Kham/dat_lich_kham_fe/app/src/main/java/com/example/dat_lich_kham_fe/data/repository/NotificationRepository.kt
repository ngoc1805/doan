package com.example.dat_lich_kham_fe.data.repository

import android.content.Context
import android.util.Log
import com.example.dat_lich_kham_fe.data.api.NotificationApi
import com.example.dat_lich_kham_fe.data.api.RetrofitInstance
import com.example.dat_lich_kham_fe.data.api.UserApi
import com.example.dat_lich_kham_fe.data.model.BaseResponse
import com.example.dat_lich_kham_fe.data.model.NotificationListResponse
import com.example.dat_lich_kham_fe.data.model.NotificationRequest
import retrofit2.Response

class NotificationRepository(private val context: Context) {
    private val notificationApi: NotificationApi by lazy {
        RetrofitInstance.notificationApi(context)
    }

    suspend fun postNotification(userId: Int, content: String, path: String): Response<BaseResponse> {
        val request = NotificationRequest(userId, content, path)
        return notificationApi.postNotification(request)
    }

    suspend fun getNotifications(userId: Int, page: Int, size: Int): Response<NotificationListResponse> {
        return notificationApi.getNotification(userId, page, size)
    }

    suspend fun markNotificationSeen(id: Int): Response<BaseResponse> {
        return notificationApi.markNotificationSeen(id)
    }

    suspend fun checkAllReceived(userId: Int): Boolean? {
        return try {
            val response = notificationApi.checkAllReceived(userId)
            Log.d("NotificationAPI", "Response code: ${response.code()}")
            Log.d("NotificationAPI", "Response body: ${response.body()}")

            if (response.isSuccessful) {
                val result = response.body()?.data?.asBoolean
                Log.d("NotificationAPI", "Parsed result: $result")
                result
            } else {
                Log.e("NotificationAPI", "Error: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("NotificationAPI", "Exception: ${e.message}")
            null
        }
    }

    suspend fun markAllReceived(userId: Int): Response<BaseResponse> {
        return notificationApi.markAllReceived(userId)
    }
}
