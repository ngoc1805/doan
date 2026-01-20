package data.repository

import data.api.RetrofitInstance
import data.model.BaseResponse
import data.model.ChangePasswordRequest
import data.model.CreateDoctorRequest
import data.model.CreateServiceRoomRequest
import retrofit2.Response

class AccountRepository {
    private val accountApi = RetrofitInstance.accountApi

    suspend fun createDoctor(name: String, code: String, departmentId: Int, examPrice: Int) : Response<BaseResponse>{
        val request = CreateDoctorRequest(name, code, departmentId, examPrice)
        return accountApi.createDoctor(request)
    }

    suspend fun changePassword(accountId: Int, oldPassword: String, newPassword: String) : Response<BaseResponse> {
        val request = ChangePasswordRequest(accountId, oldPassword, newPassword)
        return accountApi.changePassword(request)
    }

    suspend fun createServiceRoom( name: String, code: String, address: String, examPrice: Int) : Response<BaseResponse>{
        val request = CreateServiceRoomRequest(name, code, address, examPrice)
        return accountApi.createServiceRoom(request)
    }
}