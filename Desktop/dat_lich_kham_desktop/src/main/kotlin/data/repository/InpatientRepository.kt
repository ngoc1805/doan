package data.repository

import data.api.RetrofitInstance
import data.model.*
import retrofit2.Response

class InpatientRepository {
    val inpatientApi = RetrofitInstance.inpatientApi

    suspend fun creatInpatient(userId: Int) : Response<BaseResponse>{
        val request = InpatientRequest(userId)
        return inpatientApi.createInpatient(request)
    }

    suspend fun getInpatient(status: String): Response<InpatientListResponse>{
        return inpatientApi.getInpatient(status)
    }

    suspend fun updateStatus(id: Int) : Response<BaseResponse>{
        val request = UpdateStatusInpatient(id)
        return inpatientApi.updateStatus(request)
    }

    suspend fun updateAddress(id: Int, address: String) : Response<BaseResponse> {
        val request = UpdateAddressRequest(id, address)
        return inpatientApi.updateAddress(request)
    }
}