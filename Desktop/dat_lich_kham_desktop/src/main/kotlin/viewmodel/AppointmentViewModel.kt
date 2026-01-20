package viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import data.model.AppointmentByDoctorIdItem
import data.repository.AppointmentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

class AppointmentViewModel: ScreenModel {
    private val appointmentRepository = AppointmentRepository()
    var appointmentsbydoctorid by mutableStateOf<List<AppointmentByDoctorIdItem>>(emptyList())

    fun listAppointmentBydoctorId(doctorId: Int, examDate: LocalDate?, status: List<String> ){
        screenModelScope.launch(Dispatchers.Default) {
            try {
                val response = appointmentRepository.listAppointmentByDoctorId(doctorId, examDate, status)
                if(response.isSuccessful){
                    appointmentsbydoctorid = response.body()?.appointments ?: emptyList()
                }
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    fun updateStatus(appoinrmentId: Int, status: String) {
        screenModelScope.launch(Dispatchers.Default) {
            try {
                val response = appointmentRepository.updateAppointmentStatus(appoinrmentId,status)
                if(response.isSuccessful){

                }else{

                }
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }
}