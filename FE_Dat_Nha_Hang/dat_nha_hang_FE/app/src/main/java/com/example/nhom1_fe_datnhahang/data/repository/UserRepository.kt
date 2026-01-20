package com.example.nhom1_fe_datnhahang.data.repository

import android.content.Context
import com.example.nhom1_fe_datnhahang.data.api.RetrofitInstance
import com.example.nhom1_fe_datnhahang.data.api.UserApi
import com.example.nhom1_fe_datnhahang.data.model.UserResponse
import com.example.nhom1_fe_datnhahang.util.UserLocalStore
import retrofit2.Response

class UserRepository(private val context: Context) {
    private val userApi: UserApi by lazy {
        RetrofitInstance.userApi(context)
    }
    private val userStore: UserLocalStore by lazy { UserLocalStore(context) }

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
}
