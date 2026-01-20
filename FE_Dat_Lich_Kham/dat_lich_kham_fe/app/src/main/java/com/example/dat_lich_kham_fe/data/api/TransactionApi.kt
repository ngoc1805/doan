package com.example.dat_lich_kham_fe.data.api

import com.example.dat_lich_kham_fe.data.model.Transaction
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface TransactionApi {
    @GET("api/benhnhan/transactions/history/{userId}")
    suspend fun getTransactionHistory(
        @Path("userId") userId: Int
    ): Response<List<Transaction>>
}