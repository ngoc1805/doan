package com.example.dat_lich_kham_fe.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dat_lich_kham_fe.data.model.NotificationItem
import com.example.dat_lich_kham_fe.data.repository.NotificationRepository
import com.example.dat_lich_kham_fe.websocket.handlers.NotificationWebSocketHandler  // THÊM
import com.example.dat_lich_kham_fe.websocket.helpers.NotificationWebSocketHelper  // THÊM
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

    // THÊM biến này
    private var webSocketHandler: NotificationWebSocketHandler? = null
    var isWebSocketConnected by mutableStateOf(false)
        private set

    // THÊM hàm này - Cập nhật để truyền context
    // Sửa lại phần createHandler trong connectWebSocket
    fun connectWebSocket(userId: Int, baseUrl: String) {
        webSocketHandler = NotificationWebSocketHelper.createHandler(
            context = context,
            baseUrl = baseUrl,
            onNotificationReceived = { response ->
                Log.d("NotificationVM", "=== onNotificationReceived called ===")
                Log.d("NotificationVM", "Response: $response")

                val newNotifications = response.notifications
                Log.d("NotificationVM", "Received ${newNotifications.size} notifications")

                if (newNotifications.isNotEmpty()) {
                    newNotifications.forEach { noti ->
                        Log.d("NotificationVM", "Noti: id=${noti.id}, content=${noti.content}, isSeen=${noti.isSeen}")
                    }

                    Log.d("NotificationVM", "Current list size: ${notifications.size}")

                    // Kiểm tra xem đây có phải chỉ notification mới không
                    val currentIds = notifications.map { it.id }.toSet()
                    Log.d("NotificationVM", "Current IDs: $currentIds")

                    val actualNewNotifications = newNotifications.filter { !currentIds.contains(it.id) }
                    Log.d("NotificationVM", "New notifications after filter: ${actualNewNotifications.size}")

                    if (actualNewNotifications.isNotEmpty()) {
                        // Có notification mới → thêm vào đầu list
                        notifications = actualNewNotifications + notifications
                        Log.d("NotificationVM", "✓ Added ${actualNewNotifications.size} NEW notifications")
                        Log.d("NotificationVM", "New list size: ${notifications.size}")
                    } else {
                        // Không có notification mới
                        if (notifications.isEmpty()) {
                            notifications = newNotifications
                            Log.d("NotificationVM", "✓ Loaded ${newNotifications.size} notifications (fresh load)")
                        } else {
                            Log.d("NotificationVM", "No new notifications, skipping update")
                        }
                    }
                }
            },
            onConnected = {
                isWebSocketConnected = true
                Log.d("NotificationVM", "✓ WebSocket connected")
            },
            onDisconnected = {
                isWebSocketConnected = false
                Log.d("NotificationVM", "✗ WebSocket disconnected")
            },
            onError = { error ->
                Log.e("NotificationVM", "WebSocket error: $error")
            }
        )

        webSocketHandler?.connect(userId)
    }

    // THÊM hàm này
    fun disconnectWebSocket() {
        webSocketHandler?.disconnect()
        webSocketHandler = null
        isWebSocketConnected = false
    }

    // GIỮ NGUYÊN
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

    // GIỮ NGUYÊN
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
                        currentPage = 2
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

    // SỬA hàm này - thêm logic dùng WebSocket
    fun loadNextPage(userId: Int) {
        if (isLoading || !hasMore) return

        // Sử dụng WebSocket nếu đang kết nối
        webSocketHandler?.let { handler ->
            if (handler.isConnected()) {
                isLoading = true
                handler.loadMore(userId, currentPage, pageSize)
                viewModelScope.launch {
                    kotlinx.coroutines.delay(500)
                    currentPage += 1
                    isLoading = false
                }
                return
            }
        }

        // Fallback về HTTP nếu WebSocket không khả dụng
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

    // GIỮ NGUYÊN
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

    // GIỮ NGUYÊN
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

    // GIỮ NGUYÊN
    fun checkAllNotificationsReceived(userId: Int) {
        viewModelScope.launch {
            try {
                val result = notificationRepository.checkAllReceived(userId)
                allNotificationsReceived = result
            } catch (e: Exception) {
                allNotificationsReceived = null
            }
        }
    }

    // GIỮ NGUYÊN
    fun reset(userId: Int) {
        notifications = emptyList()
        currentPage = 1
        hasMore = true
        isLoading = false
        loadNotifications(userId, reset = true)
    }

    // THÊM hàm này
    override fun onCleared() {
        super.onCleared()
        disconnectWebSocket()
    }
}