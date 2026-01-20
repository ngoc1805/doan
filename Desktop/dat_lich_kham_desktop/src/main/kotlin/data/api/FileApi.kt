package data.api

import data.model.BaseResponse
import data.model.ListResultFileResponse
import data.model.SignatureVerificationResult
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface FileApi {
    @Multipart
    @POST("/api/chucnang/result-files/upload")
    suspend fun uploadResultFile(
        @Part("appointmentId") appointmentId: RequestBody,
        @Part("doctorId") doctorId: RequestBody,
        @Part("doctorName") doctorName: RequestBody,
        @Part("doctorTitle") doctorTitle: RequestBody,
        @Part file: MultipartBody.Part,
        @Part("signature") signature: RequestBody // Thêm signature
    ): Response<BaseResponse>

    @GET("/api/bacsi/result-files")
    suspend fun listFileByAppointmentId(
        @Query("appointmentId") appointmentId: Int
    ): Response<ListResultFileResponse>

    @GET("/api/bacsi/result-files/{fileId}/verify")
    suspend fun verifyFileSignature(
        @Path("fileId") fileId: Int
    ): Response<SignatureVerificationResult>

    @GET("/api/bacsi/result-files/{fileId}/download")
    @Streaming
    suspend fun downloadFile(
        @Path("fileId") fileId: Int
    ): Response<ResponseBody>
}