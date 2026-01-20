package viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import data.model.ServiceAppointmentItem
import data.repository.ServiceAppointmentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

class ServiceAppointmentViewModel : ScreenModel {
    private val serviceAppointmentRepository = ServiceAppointmentRepository()
    private val _existingRoomIds = mutableStateOf<List<Int>>(emptyList())
    val existingRoomIds = _existingRoomIds
    var serviceappointments by mutableStateOf<List<ServiceAppointmentItem>>(emptyList())

    fun createServiceAppointment(
        appointmentId: Int,
        serviceRoomId: Int,
        status: String,
        examDate: String
    ) {
        screenModelScope.launch(Dispatchers.Default) {
            try {
                val response = serviceAppointmentRepository.createServiceAppointment(
                    appointmentId, serviceRoomId, status, examDate
                )
                if (response.isSuccessful) {
                    println("✅ Tạo service appointment thành công: Room ID $serviceRoomId")
                } else {
                    println("❌ Tạo service appointment thất bại: ${response.message()}")
                }
            } catch (e: Exception) {
                println("❌ Lỗi khi tạo service appointment: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    fun getListServiceRoomId(
        appointmentId: Int,
        onResult: (List<Int>) -> Unit
    ) {
        screenModelScope.launch(Dispatchers.Default) {
            try {
                val response = serviceAppointmentRepository.getListServiceRoomId(appointmentId)
                if (response.isSuccessful && response.body() != null) {
                    val roomIds = response.body()!!
                    _existingRoomIds.value = roomIds
                    onResult(roomIds)
                    println("✅ Lấy danh sách room IDs thành công: $roomIds")
                } else {
                    onResult(emptyList())
                    println("❌ Lấy danh sách room IDs thất bại: ${response.message()}")
                }
            } catch (e: Exception) {
                onResult(emptyList())
                println("❌ Lỗi khi lấy danh sách room IDs: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    fun listServiceAppointment(serviceRoomId : Int, status: String, appointmentStatus: String?, examDate: LocalDate?) {
        screenModelScope.launch(Dispatchers.Default) {
            try {
                val response = serviceAppointmentRepository.listServiceAppointment(serviceRoomId, status, appointmentStatus, examDate)
                if(response.isSuccessful){
                    serviceappointments = response.body()?.serviceappointments ?: emptyList()
                }else{

                }
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    fun updateStatusServiceAppointment(serviceAppointmentId: Int, status: String) {
        screenModelScope.launch(Dispatchers.Default) {
            try {
                val response = serviceAppointmentRepository.updateAppointmentServiceStatus(serviceAppointmentId, status)
                if(response.isSuccessful){

                }else{

                }
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }
}