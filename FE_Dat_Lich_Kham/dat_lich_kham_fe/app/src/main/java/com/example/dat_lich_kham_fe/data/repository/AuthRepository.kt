package com.example.dat_lich_kham_fe.data.repository

import android.content.Context
import com.example.dat_lich_kham_fe.data.api.LoginApi
import com.example.dat_lich_kham_fe.data.api.RegisterApi
import com.example.dat_lich_kham_fe.data.api.RetrofitInstance
import com.example.dat_lich_kham_fe.data.model.LoginRequest
import com.example.dat_lich_kham_fe.data.model.LoginResponse
import com.example.dat_lich_kham_fe.data.model.RegisterRequest
import com.example.dat_lich_kham_fe.data.model.RegisterResponse
import com.example.dat_lich_kham_fe.util.PersistentCookieJar
import retrofit2.Response

class AuthRepository(private val context: Context) {
    private val loginApi: LoginApi by lazy {
        RetrofitInstance.loginApi(context)
    }
    private val registerApi: RegisterApi by lazy {
        RetrofitInstance.registerApi(context)
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
    suspend fun register(username: String, password: String, roleId: Int): Response<RegisterResponse> {
        val request = RegisterRequest(username = username, password = password, roleId = roleId)
        return registerApi.register(request)
    }
}
