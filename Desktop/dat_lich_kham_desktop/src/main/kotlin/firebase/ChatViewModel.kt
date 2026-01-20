package firebase

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import data.api.address
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.io.IOException

class ChatViewModel: ScreenModel {
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
        screenModelScope.launch(Dispatchers.Default) {
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

            } catch (e: HttpException) {

            } catch (e: IOException) {
            }
        }
    }
}