package com.example.dat_lich_kham_fe.data.api

import android.content.Context
import com.example.dat_lich_kham_fe.BuildConfig
import com.example.dat_lich_kham_fe.util.AuthInterceptor
import com.example.dat_lich_kham_fe.util.PersistentCookieJar
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


var address =BuildConfig.ADDRESS
var refresh = "${address}api/auth/refresh"
object RetrofitInstance {
    fun getInstance(context: Context): Retrofit {
        val cookieJar = PersistentCookieJar(context)

        val client = OkHttpClient.Builder()
            .cookieJar(cookieJar)
            .addInterceptor(AuthInterceptor(context, cookieJar))
            .build()

        return Retrofit.Builder()
            .baseUrl(address)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    fun loginApi(context: Context): LoginApi =
        getInstance(context).create(LoginApi::class.java)
    fun derpartmentApi(context: Context): DepartmentApi =
        getInstance(context).create(DepartmentApi::class.java)
    fun doctorApi(context: Context): DoctorApi =
        getInstance(context).create(DoctorApi::class.java)
    fun registerApi(context: Context): RegisterApi =
        getInstance(context).create(RegisterApi::class.java)
    fun userApi(context: Context): UserApi =
        getInstance(context).create(UserApi::class.java)
    fun appointmentApi(context: Context): AppointmentApi =
        getInstance(context).create(AppointmentApi::class.java)
    fun notificationApi(context: Context): NotificationApi =
        getInstance(context).create(NotificationApi::class.java)
    fun accountApi(context: Context): AccountApi =
        getInstance(context).create(AccountApi::class.java)
    fun serviceAppointmentApi(context: Context): ServiceAppointmentApi =
        getInstance(context).create(ServiceAppointmentApi::class.java)
    fun resultApi(context: Context): ResultApi =
        getInstance(context).create(ResultApi::class.java)
    fun inpatientApi(context: Context): InpatientApi =
        getInstance(context).create(InpatientApi::class.java)
    fun menuApi(context: Context): MenuApi =
        getInstance(context).create(MenuApi::class.java)
    fun orderApi(context: Context): OrderApi =
        getInstance(context).create(OrderApi::class.java)
    fun orderItemApi(context: Context): OrderItemApi =
        getInstance(context).create(OrderItemApi::class.java)
    fun otpApi(context: Context): OtpApi =
        getInstance(context).create(OtpApi::class.java)
    fun paymentApi(context: Context): PaymentApi =
        getInstance(context).create(PaymentApi::class.java)
    fun transactionApi(context: Context): TransactionApi =
        getInstance(context).create(TransactionApi::class.java)
}
