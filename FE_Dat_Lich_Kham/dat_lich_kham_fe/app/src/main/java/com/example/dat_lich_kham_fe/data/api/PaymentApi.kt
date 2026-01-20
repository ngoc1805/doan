package com.example.dat_lich_kham_fe.data.api

import com.example.dat_lich_kham_fe.data.model.BaseResponse
import com.example.dat_lich_kham_fe.data.model.DepositPaymentRequest
import com.example.dat_lich_kham_fe.data.model.PaymentRequest
import com.example.dat_lich_kham_fe.data.model.RefundDepositRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface PaymentApi {
    @POST("/api/benhnhan/appointment/payment")
    suspend fun paymentAppointment( @Body request: PaymentRequest ) : Response<BaseResponse>

    @POST("/api/benhnhan/deposit/payment")
    suspend fun paymentDeposit(@Body request: DepositPaymentRequest): Response<BaseResponse>

    @POST("/api/benhnhan/deposit/refund")
    suspend fun refundDeposit(@Body request: RefundDepositRequest): Response<BaseResponse>
}