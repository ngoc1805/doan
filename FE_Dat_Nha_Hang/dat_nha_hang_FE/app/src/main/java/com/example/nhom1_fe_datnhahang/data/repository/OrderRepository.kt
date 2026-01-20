package com.example.nhom1_fe_datnhahang.data.repository

import android.content.Context
import android.net.Uri
import com.example.nhom1_fe_datnhahang.data.api.OrderApi
import com.example.nhom1_fe_datnhahang.data.api.RetrofitInstance
import com.example.nhom1_fe_datnhahang.data.model.OrderWithItemsResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class OrderRepository(private val context: Context) {
    private val orderApi: OrderApi by lazy {
        RetrofitInstance.orderApi(context)
    }

    // Lấy danh sách đơn hàng kèm món ăn
    suspend fun getOrders(status: List<String?>): List<OrderWithItemsResponse>? {
        return try {
            orderApi.getOrders(status)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Cập nhật trạng thái và ảnh đơn hàng
    suspend fun updateOrderStatusAndImage(orderId: Int, imageUri: Uri): Boolean {
        return try {
            // Chuyển URI thành File
            val file = uriToFile(imageUri)

            // Tạo RequestBody cho orderId
            val orderIdBody = orderId.toString().toRequestBody("text/plain".toMediaTypeOrNull())

            // Tạo MultipartBody.Part cho ảnh
            val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("image", file.name, requestFile)

            // Gọi API
            val response = orderApi.updateOrderStatusAndImage(orderIdBody, imagePart)

            // Xóa file tạm sau khi upload
            file.delete()

            response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // Chuyển đổi URI thành File
    private fun uriToFile(uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val tempFile = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")

        inputStream?.use { input ->
            FileOutputStream(tempFile).use { output ->
                input.copyTo(output)
            }
        }

        return tempFile
    }
}
