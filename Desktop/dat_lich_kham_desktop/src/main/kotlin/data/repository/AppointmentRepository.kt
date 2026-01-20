package data.repository

import data.api.RetrofitInstance
import data.model.AppointmentByDoctorIdListResponse
import data.model.BaseResponse
import data.model.UpdateAppointmentSatatusRequest
import retrofit2.Response
import java.time.LocalDate

class AppointmentRepository {
    private val appointmentApi = RetrofitInstance.appointmentApi

    suspend fun listAppointmentByDoctorId(doctorId: Int, examDate: LocalDate?, status: List<String>): Response<AppointmentByDoctorIdListResponse> {
        return appointmentApi.listAppointmentBydoctorId(doctorId, examDate, status)
    }

    suspend fun updateAppointmentStatus(appointmentId: Int, status: String): Response<BaseResponse> {
        val request = UpdateAppointmentSatatusRequest(appointmentId, status)
        return appointmentApi.updateAppointmentStatus(request)
    }
}