package com.example.dat_lich_kham_fe.data.repository

import android.content.Context
import com.example.dat_lich_kham_fe.data.api.RetrofitInstance
import com.example.dat_lich_kham_fe.data.api.TransactionApi
import com.example.dat_lich_kham_fe.data.model.Transaction

class TransactionRepository(private val context: Context) {
    private val transactionApi: TransactionApi by lazy {
        RetrofitInstance.transactionApi(context)
    }

    suspend fun getTransactionHistory(userId: Int): Result<List<Transaction>> {
        return try {
            val response = transactionApi.getTransactionHistory(userId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Không thể tải lịch sử giao dịch: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}