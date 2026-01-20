package com.example.nhom1_fe_datnhahang.data.api

import com.example.nhom1_fe_datnhahang.data.model.BaseResponse
import com.example.nhom1_fe_datnhahang.data.model.MenuListResponse
import com.example.nhom1_fe_datnhahang.data.model.UpdateDisplayRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Query

interface MenuApi {
    @Multipart
    @POST("/api/nhaan/menus")
    suspend fun createMenu(
        @Part image: MultipartBody.Part,
        @Part("name") name: RequestBody,
        @Part("description") description: RequestBody,
        @Part("examPrice") examPrice: RequestBody,
        @Part("category") category: RequestBody
    ): BaseResponse

    @GET("/api/menus")
    suspend fun getAllMenus(@Query("isDisplay") isDisplay: Boolean? = null): Response<MenuListResponse>

    @PUT("/api/nhaan/menus/is-display")
    suspend fun updateMenuDisplay(
        @Body request: UpdateDisplayRequest
    ): Response<BaseResponse>
}

