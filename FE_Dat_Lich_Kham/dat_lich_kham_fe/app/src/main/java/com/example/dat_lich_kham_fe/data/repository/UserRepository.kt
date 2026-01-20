package com.example.dat_lich_kham_fe.data.repository

import android.content.Context
import com.example.dat_lich_kham_fe.data.api.RetrofitInstance
import com.example.dat_lich_kham_fe.data.api.UserApi
import com.example.dat_lich_kham_fe.data.model.BaseResponse
import com.example.dat_lich_kham_fe.data.model.CanteenResponse
import com.example.dat_lich_kham_fe.data.model.PinRequest
import com.example.dat_lich_kham_fe.data.model.UpdateBalanceRequest
import com.example.dat_lich_kham_fe.data.model.UserRequest
import com.example.dat_lich_kham_fe.data.model.UserResponse
import com.example.dat_lich_kham_fe.util.UserLocalStore
import retrofit2.Response

class UserRepository(private val context: Context) {
    private val userApi: UserApi by lazy {
        RetrofitInstance.userApi(context)
    }
    private val userStore: UserLocalStore by lazy { UserLocalStore(context) }
    suspend fun updateInfo(
        accountId: Int,
        fullName: String,
        gender: String,
        birthDate: String,
        cccd: String,
        hometown: String
    ) : Response<BaseResponse> {
        val request = UserRequest(
            accountId = accountId,
            fullName = fullName,
            gender = gender,
            birthDate = birthDate,
            cccd = cccd,
            hometown = hometown
            )
        return userApi.update_info(request)
    }
    //
    suspend fun getInfo(accountId: Int): Response<UserResponse> {
        val response = userApi.info(accountId)
        if (response.isSuccessful && response.body() != null) {
            val rawUser = response.body()!!
            // Đảm bảo không có giá trị null hoặc bất thường
            val safeUser = UserResponse(
                Id = rawUser.Id,
                fullName = rawUser.fullName?.ifBlank { "Chưa cập nhật" } ?: "Chưa cập nhật",
                gender = rawUser.gender?.ifBlank { "Chưa cập nhật" } ?: "Chưa cập nhật",
                birthDate = rawUser.birthDate?.ifBlank { "Chưa cập nhật" } ?: "Chưa cập nhật",
                cccd = rawUser.cccd?.ifBlank { "Chưa cập nhật" } ?: "Chưa cập nhật",
                hometown = rawUser.hometown?.ifBlank { "Chưa cập nhật" } ?: "Chưa cập nhật",
                balance = rawUser.balance,
                imageUrl = rawUser.imageUrl ?: ""
            )
            userStore.saveUser(safeUser) // Lưu vào DataStore
        } else {
            // Nếu lỗi, lưu user với dữ liệu rỗng/mặc định
            val defaultUser = UserResponse(
                Id = 0,
                fullName = "Chưa cập nhật",
                gender = "Chưa cập nhật",
                birthDate = "Chưa cập nhật",
                cccd = "Chưa cập nhật",
                hometown = "Chưa cập nhật",
                balance = 0,
                imageUrl = ""
            )
            userStore.saveUser(defaultUser)
        }
        return response
    }
    //
    suspend fun updateBalance(userId: Int, balance: Int): Response<BaseResponse> {
        val request = UpdateBalanceRequest(
            userId = userId,
            balance = balance
        )
        return userApi.update_balance(request)
    }
    //
    suspend fun hasPin(userId: Int): Response<BaseResponse> {
        return userApi.has_pin(userId)
    }
    //
    suspend fun updatePin(userId: Int, pinCode: String): Response<BaseResponse> {
        val request = PinRequest(userId, pinCode)
        return userApi.update_pin(request)
    }
    //
    suspend fun comparePin(userId: Int, pinCode: String): Response<BaseResponse> {
        val request = PinRequest(userId, pinCode)
        return userApi.compare_pin(request)
    }
    //
    suspend fun canteenInfo() : Response<CanteenResponse> {
        return userApi.cabteenInfo()
    }
}
