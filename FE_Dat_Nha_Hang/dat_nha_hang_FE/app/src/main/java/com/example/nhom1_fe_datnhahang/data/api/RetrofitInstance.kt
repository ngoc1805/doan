package com.example.nhom1_fe_datnhahang.data.api

import android.content.Context
import com.example.nhom1_fe_datnhahang.util.PersistentCookieJar
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

var address = "https://cbba364b8b10.ngrok-free.app/"
var refresh = "${address}api/auth/refresh"
object RetrofitInstance {
    fun getInstance(context: Context): Retrofit {
        val client = OkHttpClient.Builder()
            .cookieJar(PersistentCookieJar(context))
            .build()
        // 10.0.2.2
        return Retrofit.Builder()
            .baseUrl(address)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    fun menuApi(context: Context): MenuApi {
        return getInstance(context).create(MenuApi::class.java)
    }

    fun loginApi(context: Context): LoginApi {
        return getInstance(context).create(LoginApi::class.java)
    }

    fun orderApi(context: Context): OrderApi {
        return getInstance(context).create(OrderApi::class.java)
    }

    fun accountApi(context: Context): AccountApi =
        getInstance(context).create(AccountApi::class.java)

    fun userApi(context: Context): UserApi =
        getInstance(context).create(UserApi::class.java)

    fun notificationApi(context: Context): NotificationApi =
        getInstance(context).create(NotificationApi::class.java)
}
