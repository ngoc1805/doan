package com.example.dat_lich_kham_fe.data.api

import com.example.dat_lich_kham_fe.data.model.MenuListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MenuApi {
    @GET("/api/menus")
    suspend fun getAllMenus(@Query("isDisplay") isDisplay: Boolean? = null): Response<MenuListResponse>
}
