package com.example.dat_lich_kham_fe.data.repository

import android.content.Context
import com.example.dat_lich_kham_fe.data.api.RetrofitInstance

class InpatientRepository(private val context: Context) {
    private val inpatientApi = RetrofitInstance.inpatientApi(context)

    suspend fun checkAdmitted(userId: Int): Boolean? {
        val response = inpatientApi.checkAdmitted(userId)
        if (response.isSuccessful) {
            return response.body()
        }
        return null // hoặc false tùy ý
    }

    suspend fun getAddress(userId: Int): String? {
        val response = inpatientApi.getAddress(userId)
        if (response.isSuccessful) {
            return response.body()?.string()
        }
        return ""
    }
}
