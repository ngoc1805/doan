package data.repository

import data.api.RetrofitInstance
import data.model.*
import retrofit2.Response
import java.time.LocalDate

class ServiceAppointmentRepository {
    private val serviceAppointmentApi = RetrofitInstance.serviceAppointmentApi

    suspend fun createServiceAppointment(
        appointmentId: Int,
        serviceRoomId: Int,
        status: String,
        examDate: String
    ): Response<BaseResponse> {
        val request = ServiceAppointmentRequest(
            appointmentId,
            serviceRoomId,
            status,
            examDate
        )
        return serviceAppointmentApi.createServiceAppointment(request)
    }

    suspend fun getListServiceRoomId(appointmentId: Int) : Response<List<Int>> {
        return serviceAppointmentApi.getListServiceRoomId(appointmentId)
    }

    suspend fun listServiceAppointment(serviceRoomId: Int, status: String, appointmentStatus: String?, examDate: LocalDate?): Response<ListServiceAppointment> {
        return serviceAppointmentApi.listServiceAppointment(serviceRoomId, status, appointmentStatus, examDate)
    }

    suspend fun updateAppointmentServiceStatus(serviceAppointmentId: Int, status: String): Response<BaseResponse> {
        val request = UpdateServiceAppointmentStatusRequest(serviceAppointmentId, status)
        return serviceAppointmentApi.updateStatusAppointService(request)
    }


}