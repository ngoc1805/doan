package com.example.dat_lich_kham_fe.data.repository

import android.content.Context
import com.example.dat_lich_kham_fe.data.api.AppointmentApi
import com.example.dat_lich_kham_fe.data.api.RetrofitInstance
import com.example.dat_lich_kham_fe.data.api.UserApi
import com.example.dat_lich_kham_fe.data.model.AppointmentByUserIdListResponse
import com.example.dat_lich_kham_fe.data.model.AppointmentRequest
import com.example.dat_lich_kham_fe.data.model.BaseResponse
import com.example.dat_lich_kham_fe.data.model.FreeTimeRequest
import com.example.dat_lich_kham_fe.data.model.FreeTimeResponse
import com.example.dat_lich_kham_fe.data.model.UpdateAppointmentSatatusRequest
import retrofit2.Response

class AppointmentRepository(private val context: Context) {
    private val appointmentApi: AppointmentApi by lazy {
        RetrofitInstance.appointmentApi(context)
    }
    suspend fun getFreeTime(
        context: Context,
        doctorId: Int,
        date: String,
        slots: List<String>
    ): List<String> {
        val api = RetrofitInstance.appointmentApi(context)
        return try {
            val resp: Response<FreeTimeResponse> = api.freetime(
                FreeTimeRequest(
                    doctorId = doctorId,
                    date = date,
                    slots = slots
                )
            )
            if (resp.isSuccessful) {
                resp.body()?.freeSlots ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun bookAppointment(
        userId: Int,
        doctorId: Int,
        examDate: String,
        examTime: String,
        status: String,
    ): Response<BaseResponse>{
        val request = AppointmentRequest(
            userId = userId,
            doctorId = doctorId,
            examDate = examDate,
            examTime = examTime,
            status = status
        )
        return appointmentApi.bookAppointment(request)
    }

    suspend fun listAppointmentByUserId(userId: Int, status: List<String?>): Response<AppointmentByUserIdListResponse> {
        return appointmentApi.listAppointments(userId, status)
    }

    suspend fun updateAppointmentStatus(appointmentId: Int, status: String): Response<BaseResponse> {
        val request = UpdateAppointmentSatatusRequest(appointmentId, status)
        return appointmentApi.updateAppointmentStatus(request)
    }
}
