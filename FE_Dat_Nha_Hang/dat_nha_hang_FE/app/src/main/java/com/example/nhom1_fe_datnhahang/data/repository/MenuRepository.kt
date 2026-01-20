package com.example.nhom1_fe_datnhahang.data.repository

import android.content.Context
import com.example.nhom1_fe_datnhahang.data.api.MenuApi
import com.example.nhom1_fe_datnhahang.data.api.RetrofitInstance
import com.example.nhom1_fe_datnhahang.data.model.BaseResponse
import com.example.nhom1_fe_datnhahang.data.model.MenuListResponse
import com.example.nhom1_fe_datnhahang.data.model.UpdateDisplayRequest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.io.File

class MenuRepository(private val context: Context) {

    private val menuApi: MenuApi by lazy {
        RetrofitInstance.menuApi(context)
    }

    suspend fun createMenu(
        imageFile: File,
        name: String,
        description: String,
        examPrice: Int,
        category: String
    ): BaseResponse {
        // Chuẩn bị part cho hình ảnh
        val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), imageFile)
        val imagePart = MultipartBody.Part.createFormData("image", imageFile.name, requestFile)

        // Chuẩn bị các part text
        val namePart = RequestBody.create("text/plain".toMediaTypeOrNull(), name)
        val descriptionPart = RequestBody.create("text/plain".toMediaTypeOrNull(), description)
        val pricePart = RequestBody.create("text/plain".toMediaTypeOrNull(), examPrice.toString())
        val categoryPart = RequestBody.create("text/plain".toMediaTypeOrNull(), category)

        // Gọi API
        return menuApi.createMenu(
            image = imagePart,
            name = namePart,
            description = descriptionPart,
            examPrice = pricePart,
            category = categoryPart
        )
    }

    suspend fun getAllMenu(isDisplay: Boolean?): Response<MenuListResponse> {
        return menuApi.getAllMenus(isDisplay)
    }

    suspend fun updateMenuDisplay(id : Int, isDisplay: Boolean): Response<BaseResponse> {
        val request = UpdateDisplayRequest(id, isDisplay)
        return menuApi.updateMenuDisplay(request)
    }
}
