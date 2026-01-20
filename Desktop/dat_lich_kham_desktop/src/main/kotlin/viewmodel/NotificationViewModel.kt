package viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import data.repository.NotificationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class NotificationViewModel : ScreenModel {
    private val notificationRepository = NotificationRepository()

    fun postNotification(userId: Int, content: String, path: String) {
        screenModelScope.launch(Dispatchers.Default) {
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
}