package data.repository

import data.api.RetrofitInstance
import data.model.BaseResponse
import data.model.ListResultFileResponse
import data.model.SignatureVerificationResult
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response

class FileRepository {
    private val fileApi = RetrofitInstance.fileApi

    suspend fun uploadResultFile(
        appointmentId: Int,
        doctorId: Int,
        doctorName: String,
        doctorTitle: String = "Bác sĩ",
        file: MultipartBody.Part,
        signature: RequestBody // Thêm tham số signature
    ): Response<BaseResponse> {
        val appointmentIdBody = RequestBody.create(
            "text/plain".toMediaTypeOrNull(),
            appointmentId.toString()
        )
        val doctorIdBody = RequestBody.create(
            "text/plain".toMediaTypeOrNull(),
            doctorId.toString()
        )
        val doctorNameBody = RequestBody.create(
            "text/plain".toMediaTypeOrNull(),
            doctorName
        )
        val doctorTitleBody = RequestBody.create(
            "text/plain".toMediaTypeOrNull(),
            doctorTitle
        )

        return fileApi.uploadResultFile(
            appointmentIdBody,
            doctorIdBody,
            doctorNameBody,
            doctorTitleBody,
            file,
            signature // Thêm signature vào API call
        )
    }

    suspend fun listFileByAppointmentId(appointmentId: Int): Response<ListResultFileResponse> {
        return fileApi.listFileByAppointmentId(appointmentId)
    }

    suspend fun verifyFileSignature(fileId: Int): Response<SignatureVerificationResult> {
        return fileApi.verifyFileSignature(fileId)
    }

    suspend fun downloadFile(fileId: Int): Response<ResponseBody> {
        return fileApi.downloadFile(fileId)
    }
}