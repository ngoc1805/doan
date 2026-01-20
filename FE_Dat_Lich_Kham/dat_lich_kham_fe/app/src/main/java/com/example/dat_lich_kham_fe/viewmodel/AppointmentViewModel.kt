package com.example.dat_lich_kham_fe.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dat_lich_kham_fe.data.model.AppointmentItem
import com.example.dat_lich_kham_fe.data.model.NotificationItem
import com.example.dat_lich_kham_fe.data.repository.AppointmentRepository
import kotlinx.coroutines.launch

class AppointmentViewModel(private val context: Context) : ViewModel() {

    private val appointmentRepository = AppointmentRepository(context)
    var appointments by mutableStateOf<List<AppointmentItem>>(emptyList())

    fun bookappointment(
        userId: Int,
        doctorId: Int,
        examDate: String,
        examTime: String,
        status: String,
    ) {
        viewModelScope.launch {
            try {
                val response = appointmentRepository.bookAppointment(
                    userId,
                    doctorId,
                    examDate,
                    examTime,
                    status
                )
                if(response.isSuccessful){
                    val responseBody = response.body()
                    // Xử lý phản hồi thành công
                } else {
                    // Xử lý lỗi từ máy chủ
                }
            }catch (e: Exception){
                e.printStackTrace()
            }
        }

    }
    fun listappontment(userId: Int, status: List<String>) {
        viewModelScope.launch {
            try {
                val response = appointmentRepository.listAppointmentByUserId(userId,status)
                if(response.isSuccessful){
                    appointments = response.body()?.appointments ?: emptyList()
                }
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }
    fun updateStatus(appoinrmentId: Int, status: String) {
        viewModelScope.launch {
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
