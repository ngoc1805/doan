package com.example.dat_lich_kham_fe.viewmodel

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.dat_lich_kham_fe.data.repository.UserRepository
import com.example.dat_lich_kham_fe.util.PersistentCookieJar
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class InFoViewModel(private val context: Context) : ViewModel() {
    var fullName by mutableStateOf("")
        private set
    var gender by mutableStateOf("")
        private set
    var birthDate by mutableStateOf<LocalDate?>(null)
        private set
    var cccd by mutableStateOf("")
        private set
    var hometown by mutableStateOf("")
        private set
    var fullNameError by mutableStateOf<String?>(null)
        private set
    var genderError by mutableStateOf<String?>(null)
        private set
    var birthDateError by mutableStateOf<String?>(null)
        private set
    var cccdError by mutableStateOf<String?>(null)
        private set
    var hometownError by mutableStateOf<String?>(null)
        private set
    var isLoading by mutableStateOf(false)
        private set
    var updateError by mutableStateOf<String?>(null)
        private set
    var updateSuccess by mutableStateOf<Boolean?>(null)
        private set
    var hasQrScanResult by mutableStateOf<Boolean>(false)
        private set

    private val repository = UserRepository(context)
    private val cookieJar = PersistentCookieJar(context)

    fun onFullNameChanged(newFullName: String) {
        fullName = newFullName
        fullNameError = null
    }
    fun onGenderChanged(newGender: String) {
        gender = newGender
        genderError = null
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun onBirthDateChanged(newBirthDate: String) {
        birthDate = try {
            LocalDate.parse(newBirthDate, DateTimeFormatter.ofPattern("ddMMyyyy"))
        } catch (e: Exception) {
            null
        }
        birthDateError = null
    }
    fun onCccdChanged(newCccd: String) {
        cccd = newCccd
        cccdError = null
    }
    fun onHometownChanged(newHometown: String) {
        hometown = newHometown
        hometownError = null
    }
    fun onHasQrScanResultChanged(hasResult: Boolean) {
        hasQrScanResult = hasResult
    }

    private val userRepository = UserRepository(context)

    @RequiresApi(Build.VERSION_CODES.O)
    fun parseQrResult(qrResult: String) {
        val fields = qrResult.split("|")
        if (fields.size >= 5) {
            cccd = fields[0].trim()
            fullName = fields[2].trim()
            birthDate = try {
                LocalDate.parse(fields[3].trim(), DateTimeFormatter.ofPattern("ddMMyyyy"))
            } catch (e: Exception) { null }
            gender = fields[4].trim()
            hometown = fields[5].trim()
            hasQrScanResult = true
        } else {
            cccd = ""
            fullName = ""
            birthDate = null
            gender = ""
            hometown = ""
        }
    }

    // Hàm validate các trường dữ liệu
    @RequiresApi(Build.VERSION_CODES.O)
    fun validateAndUpdate(onSuccess: () -> Unit) {
        fullNameError = if (fullName.isBlank()) "Họ tên không được để trống" else null
        genderError = if (gender.isBlank()) "Vui lòng chọn giới tính" else null
        birthDateError = if (birthDate == null) "Vui lòng nhập ngày sinh hợp lệ" else null
        cccdError = if (cccd.length != 12) "CCCD phải đủ 12 số" else null
        hometownError = if (hometown.isBlank()) "Quê quán không được để trống" else null

        if (listOf(fullNameError, genderError, birthDateError, cccdError, hometownError).all { it == null }) {
            isLoading = true
            updateError = null
            updateSuccess = null
            viewModelScope.launch {
                try {
                    // Lấy accountId từ DataStore (cookieJar)
                    val accountId = cookieJar.getAccountId()?.toIntOrNull()
                    if (accountId == null) {
                        updateError = "Không lấy được ID tài khoản!"
                        isLoading = false
                        return@launch
                    }
                    val response = repository.updateInfo(
                        accountId = accountId,
                        fullName = fullName,
                        gender = gender,
                        birthDate = birthDate!!.toString(),
                        cccd = cccd,
                        hometown = hometown
                    )
                    if (response.isSuccessful && response.body()?.success == true) {
                        // Lưu trạng thái đã cập nhật info
                        cookieJar.saveInfoUpdated(true)
                        updateSuccess = true
                        if (accountId != null) {
                            userRepository.getInfo(accountId)
                            // Sau dòng này, dữ liệu đã được lưu vào UserLocalStore (DataStore)
                        }
                        onSuccess()
                    } else {
                        // Lấy lỗi cụ thể từ message hoặc errorBody
                        val messageFromBody = response.body()?.message
                        val messageFromErrorBody = response.errorBody()?.let { errorBody ->
                            try {
                                val raw = errorBody.string()
                                JSONObject(raw).optString("message", "Cập nhật thất bại")
                            } catch (e: Exception) {
                                "Cập nhật thất bại"
                            }
                        }
                        updateError = messageFromBody ?: messageFromErrorBody ?: "Cập nhật thất bại"
                        updateSuccess = false
                    }
                } catch (e: Exception) {
                    updateError = "Có lỗi xảy ra: ${e.message}"
                    updateSuccess = false
                }
                isLoading = false
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadUserInfoFromLocal() {
        val userStore = com.example.dat_lich_kham_fe.util.UserLocalStore(context)
        viewModelScope.launch {
            val user = userStore.getUser()
            if (user != null) {
                fullName = user.fullName
                gender = user.gender
                birthDate = try { java.time.LocalDate.parse(user.birthDate) } catch (e: Exception) { null }
                cccd = user.cccd
                hometown = user.hometown
            }
        }
    }

    fun reset() {
        fullName = ""
        gender = ""
        birthDate = null
        cccd = ""
        hometown = ""
        fullNameError = null
        genderError = null
        birthDateError = null
        cccdError = null
        hometownError = null
        isLoading = false
        updateError = null
        updateSuccess = null
        hasQrScanResult = false
    }
}
class InFoViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InFoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InFoViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
