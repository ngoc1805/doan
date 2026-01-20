package com.example.dat_lich_kham_fe.data.api

import com.example.dat_lich_kham_fe.data.model.ResetPasswordRequest
import com.example.dat_lich_kham_fe.data.model.ResetPasswordResponse
import com.example.dat_lich_kham_fe.data.model.ResetPinRequest
import com.example.dat_lich_kham_fe.data.model.ResetPinResponse
import com.example.dat_lich_kham_fe.data.model.SendOtpRequest
import com.example.dat_lich_kham_fe.data.model.SendOtpResponse
import com.example.dat_lich_kham_fe.data.model.VerifyOtpForResetRequest
import com.example.dat_lich_kham_fe.data.model.VerifyOtpForResetResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface OtpApi {
    @POST("/send_otp")
    suspend fun sendOtp(
        @Body request: SendOtpRequest
    ): Response<SendOtpResponse>

    @POST("/verify_otp_for_reset")
    suspend fun verifyOtpForReset(
        @Body request: VerifyOtpForResetRequest
    ): Response<VerifyOtpForResetResponse>

    @POST("/reset_password")
    suspend fun resetPassword(
        @Body request: ResetPasswordRequest
    ): Response<ResetPasswordResponse>

    @POST("/reset_pin")
    suspend fun resetPin(
        @Body request: ResetPinRequest
    ): Response<ResetPinResponse>
}
