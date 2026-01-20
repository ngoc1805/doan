package com.example.dat_lich_kham_fe.data.repository

import android.content.Context
import com.example.dat_lich_kham_fe.data.api.PaymentApi
import com.example.dat_lich_kham_fe.data.api.RetrofitInstance
import com.example.dat_lich_kham_fe.data.model.BaseResponse
import com.example.dat_lich_kham_fe.data.model.DepositPaymentRequest
import com.example.dat_lich_kham_fe.data.model.PaymentRequest
import com.example.dat_lich_kham_fe.data.model.RefundDepositRequest
import retrofit2.Response

class PaymentRepository(private val context: Context) {
    private val paymentApi : PaymentApi by lazy {
        RetrofitInstance.paymentApi(context)
    }

    suspend fun paymentAppointment(appointmentId: Int): Response<BaseResponse> {
        return try {
            paymentApi.paymentAppointment(
                PaymentRequest(appointmentId = appointmentId)
            )
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun paymentDeposit(userId: Int): Response<BaseResponse> {
        return try {
            paymentApi.paymentDeposit(
                DepositPaymentRequest(userId = userId)
            )
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun refundDeposit(userId: Int): Response<BaseResponse> {
        return try {
            paymentApi.refundDeposit(
                RefundDepositRequest(userId = userId)
            )
        } catch (e: Exception) {
            throw e
        }
    }

    // Hàm kiểm tra response có thành công không
    fun isPaymentSuccessful(response: Response<BaseResponse>): Boolean {
        return response.isSuccessful && response.body()?.success == true
    }

    // Lấy message từ response
    fun getResponseMessage(response: Response<BaseResponse>): String {
        return response.body()?.message ?: "Có lỗi xảy ra"
    }
}