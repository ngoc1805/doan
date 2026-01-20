package com.example.dat_lich_kham_fe.data.repository

import android.content.Context
import com.example.dat_lich_kham_fe.data.api.RetrofitInstance
import com.example.dat_lich_kham_fe.data.api.ServiceAppointmentApi
import com.example.dat_lich_kham_fe.data.model.ListServiceItemResponse
import retrofit2.Response

class ServiceAppointmentRepository(private val context: Context) {
    private val serviceAppointmentApi : ServiceAppointmentApi by lazy {
        RetrofitInstance.serviceAppointmentApi(context)
    }

    suspend fun getServiceAppointment(appointmentId : Int) : Response<ListServiceItemResponse> {
        return serviceAppointmentApi.getServicrAppointment(appointmentId)
    }
}
