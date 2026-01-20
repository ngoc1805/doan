package com.example.dat_lich_kham_fe.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dat_lich_kham_fe.data.model.ServiceRoomItem
import com.example.dat_lich_kham_fe.data.repository.ServiceAppointmentRepository
import kotlinx.coroutines.launch

class ServiceAppointmentViewModel(private val context: Context) : ViewModel() {
    private val serviceAppointmentRepository = ServiceAppointmentRepository(context)
    var serviceAppointments by mutableStateOf<List<ServiceRoomItem>>(emptyList())

    fun getServiceAppointments(appointmentId : Int) {
        viewModelScope.launch {
            try {
                val response = serviceAppointmentRepository.getServiceAppointment(appointmentId)
                if(response.isSuccessful) {
                    val list = response.body()?.servicerooms?: emptyList()
                    serviceAppointments = list
                }else{
                    println("Error: ${response.code()}")
                }
            }catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
