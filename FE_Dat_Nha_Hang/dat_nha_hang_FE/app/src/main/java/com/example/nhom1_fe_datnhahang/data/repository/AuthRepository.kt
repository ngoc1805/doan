package com.example.nhom1_fe_datnhahang.data.repository

import android.content.Context
import com.example.nhom1_fe_datnhahang.data.api.LoginApi
import com.example.nhom1_fe_datnhahang.data.api.RetrofitInstance
import com.example.nhom1_fe_datnhahang.data.model.LoginRequest
import com.example.nhom1_fe_datnhahang.data.model.LoginResponse
import com.example.nhom1_fe_datnhahang.util.PersistentCookieJar
import retrofit2.Response

class AuthRepository(private val context: Context) {
    private val loginApi: LoginApi by lazy {
        RetrofitInstance.loginApi(context)
    }


    suspend fun login(username: String, password: String): Response<LoginResponse> {
        val request = LoginRequest(username = username, password = password)
        return loginApi.login(request)
    }

    suspend fun isLoggedIn(): Boolean {
        return PersistentCookieJar(context).isLoggedIn()
    }

    suspend fun logout() {
        PersistentCookieJar(context).logout()
    }
}
