package com.example.dat_lich_kham_fe.data.repository

import android.content.Context
import com.example.dat_lich_kham_fe.data.api.RetrofitInstance
import com.example.dat_lich_kham_fe.data.model.InpatientItem

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

    suspend fun getCurrentInpatient(userId: Int): com.example.dat_lich_kham_fe.data.model.InpatientItem? {
        return try {
            val response = inpatientApi.getCurrentInpatient(userId)
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    suspend fun getInpatientHistory(userId: Int): List<InpatientItem>? {
        return try {
            val response = inpatientApi.getInpatientHistory(userId)
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
