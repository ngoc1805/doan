package com.example.nhom1_fe_datnhahang.Firebase

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nhom1_fe_datnhahang.data.api.address
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.io.IOException

class ChatViewModel: ViewModel() {
    var state by mutableStateOf(ChatState())
        private set

    private val api: FcmApi = Retrofit.Builder()
        .baseUrl(address)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create()

    fun onRemoteTokenChange(newToken: String){
        state = state.copy(
            remoteToken = newToken
        )
    }
    fun onSubmitRemoteToken(){
        state = state.copy(
            isEnteringToken = false
        )
    }
    fun onMessageChange(message: String){
        state = state.copy(
            messageText = message
        )
    }
    fun sendMessage(title: String, body: String, remoteToken: String, isBroadcast: Boolean) {
        viewModelScope.launch {
            val messageDto = SendMessageDto(
                to = if (isBroadcast) null else remoteToken,
                notification = NotificationBody(
                    title = title,
                    body = body
                )
            )
            try {
                if (isBroadcast) {
                    api.broadcast(messageDto)
                } else {
                    api.sendMessage(messageDto)
                }
                Log.d("FCM", "Message sent: $title - $body")
            } catch (e: HttpException) {
                Log.e("FCM", "HTTP Error: ${e.message}")
            } catch (e: IOException) {
                Log.e("FCM", "Network Error: ${e.message}")
            }
        }
    }
}
