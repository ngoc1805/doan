package viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import data.model.BaseResponse
import data.model.ResultFileItem
import data.repository.FileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.Signature
import java.security.spec.PKCS8EncodedKeySpec
import java.util.Base64

sealed class FileUploadState {
    object Initial : FileUploadState()
    object Loading : FileUploadState()
    data class Success(val response: BaseResponse) : FileUploadState()
    data class Error(val message: String) : FileUploadState()
}

class FileViewModel() : ScreenModel {
    private val fileRepository = FileRepository()
    private val _uploadState = MutableStateFlow<FileUploadState>(FileUploadState.Initial)
    val uploadState: StateFlow<FileUploadState> = _uploadState

    var resultfiles by mutableStateOf<List<ResultFileItem>>(emptyList())

    // Đường dẫn private key
    private val privateKeyPath = "C:\\Dat_Lich_Kham\\dat_lich_kham_be\\keys\\hospital_private.pem"

    // lay private key
    private fun loadPrivateKey(): PrivateKey {
        val keyFile = File(privateKeyPath)
        if (!keyFile.exists()) {
            throw Exception("Private key không tồn tại tại: $privateKeyPath")
        }

        val keyContent = keyFile.readText()
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replace("\\s".toRegex(), "")

        val keyBytes = Base64.getDecoder().decode(keyContent)
        val keySpec = PKCS8EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance("RSA")
        return keyFactory.generatePrivate(keySpec)
    }

    // tao chu ky so
    private fun signFile(fileBytes: ByteArray): String {
        val privateKey = loadPrivateKey()
        val signature = Signature.getInstance("SHA256withRSA")
        signature.initSign(privateKey)
        signature.update(fileBytes)
        val signatureBytes = signature.sign()
        return Base64.getEncoder().encodeToString(signatureBytes)
    }

    fun uploadResultFile(
        appointmentId: Int,
        doctorId: Int,
        doctorName: String,
        doctorTitle: String = "Bác sĩ",
        file: MultipartBody.Part,
        originalFile: File // Thêm tham số này để đọc file gốc
    ) {
        _uploadState.value = FileUploadState.Loading
        screenModelScope.launch(Dispatchers.Default) {
            try {
                // 1. Đọc file và tạo chữ ký
                val fileBytes = originalFile.readBytes()
                val fileSignature = signFile(fileBytes)

                println(" Đã tạo chữ ký cho file: ${originalFile.name}")
                println(" Signature: ${fileSignature.take(50)}...")

                // 2. Tạo RequestBody cho signature
                val signatureBody = fileSignature.toRequestBody("text/plain".toMediaTypeOrNull())

                // 3. Upload file kèm signature
                val response = fileRepository.uploadResultFile(
                    appointmentId,
                    doctorId,
                    doctorName,
                    doctorTitle,
                    file,
                    signatureBody // Thêm signature vào request
                )

                if (response.isSuccessful && response.body() != null) {
                    _uploadState.value = FileUploadState.Success(response.body()!!)
                } else {
                    _uploadState.value = FileUploadState.Error(response.errorBody()?.string() ?: "Unknown error")
                }
            } catch (e: Exception) {
                _uploadState.value = FileUploadState.Error(e.message ?: "Exception error")
                e.printStackTrace()
            }
        }
    }

    fun listFileByAppointmentId(appointmentId: Int) {
        screenModelScope.launch(Dispatchers.Default) {
            try {
                val response = fileRepository.listFileByAppointmentId(appointmentId)
                if (response.isSuccessful){
                    resultfiles = response.body()?.resultfiles ?: emptyList()
                }
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    fun resetState() {
        _uploadState.value = FileUploadState.Initial
    }
}