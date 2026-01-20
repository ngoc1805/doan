package com.example.dat_lich_kham_fe.data.repository

import android.content.Context
import com.example.dat_lich_kham_fe.data.api.AccountApi
import com.example.dat_lich_kham_fe.data.api.RetrofitInstance
import com.example.dat_lich_kham_fe.data.model.BaseResponse
import com.example.dat_lich_kham_fe.data.model.ChangePasswordRequest
import com.example.dat_lich_kham_fe.data.model.UpdateFmcTokenRequest
import retrofit2.Response

class AccountRepository(private val context: Context) {
    private val accountApi: AccountApi by lazy {
        RetrofitInstance.accountApi(context)
    }
    suspend fun updateFmcToken(accountId: Int, fmcToken: String) : Response<BaseResponse> {
        val request = UpdateFmcTokenRequest(accountId, fmcToken)
        return accountApi.updateFmcToken(request)
    }

    suspend fun changePassword(accountId: Int, oldPassword: String, newPassword: String) : Response<BaseResponse> {
        val request = ChangePasswordRequest(accountId, oldPassword, newPassword)
        return accountApi.changePassword(request)
    }
}
