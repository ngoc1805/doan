package viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import data.model.InFoDoctorResponse
import data.model.ServiceRoomItem
import data.model.ServiceRoomResponse
import data.repository.ServiceRoomRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ServiceRoomViewModel: ScreenModel {
    private val serviceRoomRepository = ServiceRoomRepository()

    var servicerooms by mutableStateOf<List<ServiceRoomItem>>(emptyList())

    private val _serviceroomInfoState = MutableStateFlow<ServiceRoomResponse?>(null)
    val doctorInfoState: StateFlow<ServiceRoomResponse?> = _serviceroomInfoState

    fun listServiceRoom() {
        screenModelScope.launch(Dispatchers.Default) {
            try {
                val response = serviceRoomRepository.listServiceRoom()
                if(response.isSuccessful) {
                    servicerooms = response.body()?.servicerooms ?: emptyList()
                }
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    fun fetchServiceRoomInfo(accountId: Int) {
        screenModelScope.launch(Dispatchers.Default) {
            val response = serviceRoomRepository.getServiceRoomByAccountId(accountId)
            if (response.isSuccessful) {
                _serviceroomInfoState.value = response.body()
                // DoctorRepository đã lưu vào file rồi
            } else {
                _serviceroomInfoState.value = null
            }
        }
    }
}