package data.repository

import data.api.RetrofitInstance
import data.model.BaseResponse
import data.model.CreateDepartmentRequest
import data.model.DepartmentResponse
import retrofit2.Response

class DepartmentRepository {
    private val departmentApi = RetrofitInstance.departmentApi

    suspend fun listDepartment() : List<DepartmentResponse> {
        return departmentApi.departments()
    }

    suspend fun createDepartment(name: String, description: String) : Response<BaseResponse>{
        val request = CreateDepartmentRequest(name, description)
        return departmentApi.createDepartment(request)
    }
}