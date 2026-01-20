package com.example.nhom1_fe_datnhahang.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nhom1_fe_datnhahang.data.model.NotificationItem
import com.example.nhom1_fe_datnhahang.data.repository.NotificationRepository
import kotlinx.coroutines.launch
import org.json.JSONObject

class NotificationViewModel(private val context: Context): ViewModel() {
    private val notificationRepository = NotificationRepository(context)
    var notifications by mutableStateOf<List<NotificationItem>>(emptyList())
    var currentPage by mutableStateOf(1)
        private set
    val pageSize = 30
    var isLoading by mutableStateOf(false)
        private set
    var hasMore by mutableStateOf(true)
        private set
    var allNotificationsReceived by mutableStateOf<Boolean?>(null)
        private set


    fun postNotification(userId: Int, content: String, path: String) {
        viewModelScope.launch {
            try {
                val response = notificationRepository.postNotification(userId, content, path)
                if (response.isSuccessful) {

                }
                else{
                    val messageFromBody = response.body()?.message
                    val messageFromErrorBody = response.errorBody()?.let { errorBody ->
                        try {
                            val raw = errorBody.string()
                            JSONObject(raw).optString("message", "Cập nhật thất bại")
                        } catch (e: Exception) {
                            "Cập nhật thất bại"
                        }
                    }
                }
            }catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    fun loadNotifications(userId: Int, reset: Boolean = false) {
        if (isLoading || !hasMore) return
        isLoading = true
        viewModelScope.launch {
            try {
                val response = notificationRepository.getNotifications(userId, currentPage, pageSize)
                if (response.isSuccessful) {
                    val list = response.body()?.notifications ?: emptyList()
                    if (reset) {
                        notifications = list
                        currentPage = 2 // Trang sau khi load lần đầu phải là 2
                    } else {
                        notifications = notifications + list
                        currentPage += 1
                    }
                    hasMore = list.size == pageSize
                } else {
                    hasMore = false
                }
            } catch (e: Exception) {
                hasMore = false
            } finally {
                isLoading = false
            }
        }
    }

    fun loadNextPage(userId: Int) {
        if (isLoading || !hasMore) return
        isLoading = true
        viewModelScope.launch {
            try {
                val response = notificationRepository.getNotifications(userId, currentPage, pageSize)
                if (response.isSuccessful) {
                    val list = response.body()?.notifications ?: emptyList()
                    notifications = notifications + list
                    currentPage += 1
                    hasMore = list.size == pageSize
                } else {
                    hasMore = false
                }
            } catch (e: Exception) {
                hasMore = false
            } finally {
                isLoading = false
            }
        }
    }

    fun markNotificationSeen(id: Int, onResult: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            try {
                val response = notificationRepository.markNotificationSeen(id)
                if (response.isSuccessful) {
                    notifications = notifications.map { noti ->
                        if (noti.id == id) noti.copy(isSeen = true) else noti
                    }
                    onResult(true)
                } else {
                    onResult(false)
                }
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }

    fun markAllReceived(userId: Int) {
        viewModelScope.launch {
            try {
                val response = notificationRepository.markAllReceived(userId)
                if (response.isSuccessful) {

                } else {

                }
            } catch (e: Exception) {

            }
        }
    }

    fun checkAllNotificationsReceived(userId: Int) {
        viewModelScope.launch {
            try {
                val result = notificationRepository.checkAllReceived(userId)
                allNotificationsReceived = result // true/false hoặc null nếu lỗi
            } catch (e: Exception) {
                allNotificationsReceived = null
            }
        }
    }

    fun reset(userId: Int) {
        notifications = emptyList()
        currentPage = 1
        hasMore = true
        isLoading = false
        loadNotifications(userId, reset = true)
    }
}
