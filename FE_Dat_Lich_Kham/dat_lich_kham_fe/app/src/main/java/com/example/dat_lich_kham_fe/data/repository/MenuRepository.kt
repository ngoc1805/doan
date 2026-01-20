package com.example.dat_lich_kham_fe.data.repository

import android.content.Context
import com.example.dat_lich_kham_fe.data.api.MenuApi
import com.example.dat_lich_kham_fe.data.api.RetrofitInstance
import com.example.dat_lich_kham_fe.data.model.MenuListResponse
import retrofit2.Response

class MenuRepository(private val context: Context) {
    private val menuApi : MenuApi by lazy {
        RetrofitInstance.menuApi(context)
    }

    suspend fun getAllMenu(isDisplay: Boolean?): Response<MenuListResponse> {
        return menuApi.getAllMenus(isDisplay)
    }
}
