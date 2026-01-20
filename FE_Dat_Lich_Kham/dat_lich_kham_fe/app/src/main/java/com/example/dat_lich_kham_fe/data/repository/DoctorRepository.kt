package com.example.dat_lich_kham_fe.repository

import android.content.Context
import com.example.dat_lich_kham_fe.cache.DoctorCache
import com.example.dat_lich_kham_fe.data.api.RetrofitInstance
import com.example.dat_lich_kham_fe.data.model.DoctorResponse
import com.example.dat_lich_kham_fe.data.model.DoctorListResponse

class DoctorRepository(private val context: Context) {
    private val doctorApi = RetrofitInstance.doctorApi(context)

    suspend fun getDoctorsByDepartment(
        departmentId: Int,
        page: Int = 1,
        size: Int = 20
    ): List<DoctorResponse> {
        val key = Pair(departmentId, page)
        DoctorCache.doctorsMap[key]?.let {
            return it
        }
        val response: DoctorListResponse = doctorApi.getDoctorsByDepartment(departmentId, page, size)
        DoctorCache.doctorsMap[key] = response.doctors
        return response.doctors
    }
}
