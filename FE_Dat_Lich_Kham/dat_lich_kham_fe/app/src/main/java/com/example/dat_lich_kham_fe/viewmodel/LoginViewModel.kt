package com.example.dat_lich_kham_fe.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dat_lich_kham_fe.data.api.RetrofitInstance
import com.example.dat_lich_kham_fe.data.repository.AuthRepository
import com.example.dat_lich_kham_fe.data.model.LoginErrorResponse
import com.example.dat_lich_kham_fe.data.model.LoginLockedResponse
import com.example.dat_lich_kham_fe.data.repository.UserRepository
import com.example.dat_lich_kham_fe.util.PersistentCookieJar
import com.google.gson.Gson
import kotlinx.coroutines.launch

class LoginViewModel(private val context: Context) : ViewModel() {

    private val cookieJar = PersistentCookieJar(context)
    private val userApi = RetrofitInstance.userApi(context)

    var numberPhone by mutableStateOf("")
        private set

    var passWord by mutableStateOf("")
        private set

    var isLoginSuccessful by mutableStateOf(false)
        private set

    var phoneNumberError by mutableStateOf<String?>(null)
        private set

    var passwordError by mutableStateOf<String?>(null)
        private set

    var loginError by mutableStateOf<String?>(null)
        private set

    // ✅ THÊM STATE CHO BẢO MẬT
    var attemptsRemaining by mutableStateOf<Int?>(null)
        private set

    var isAccountLocked by mutableStateOf(false)
        private set

    var lockRemainingTime by mutableStateOf<Long?>(null)
        private set

    private val auRepository = AuthRepository(context)
    private val userRepository = UserRepository(context)
    private val gson = Gson()

    fun onNumberPhoneChanged(newNumberPhone: String) {
        numberPhone = newNumberPhone
        phoneNumberError = null
        // Reset security states khi user nhập lại
        loginError = null
        attemptsRemaining = null
        isAccountLocked = false
        lockRemainingTime = null
    }

    fun onPassWordChanged(newPassWord: String) {
        passWord = newPassWord
        passwordError = null
    }

    fun validateAndLogin(onSuccess: () -> Unit, onFail: (String) -> Unit, onLocked: (String, Long) -> Unit) {
        val isPhoneNotEmpty = numberPhone.isNotEmpty()
        val isPhoneValidLength = numberPhone.length == 10
        val isPhoneNumberic = numberPhone.all { it.isDigit() }
        val isPasswordNotEmpty = passWord.isNotEmpty()
        val isPasswordValidLength = passWord.length >= 6

        phoneNumberError = when {
            !isPhoneNotEmpty -> "Số điện thoại không được bỏ trống"
            !isPhoneValidLength -> "Số điện thoại phải đúng 10 ký tự"
            !isPhoneNumberic -> "Số điện thoại chỉ bao gồm các số"
            else -> null
        }

        passwordError = when {
            !isPasswordNotEmpty -> "Mật khẩu không được bỏ trống"
            !isPasswordValidLength -> "Mật khẩu tối thiểu 6 ký tự"
            else -> null
        }

        if (phoneNumberError == null && passwordError == null) {
            viewModelScope.launch {
                try {
                    val response = auRepository.login(numberPhone, passWord)

                    when (response.code()) {
                        // ✅ 200 - ĐĂNG NHẬP THÀNH CÔNG
                        200 -> {
                            isLoginSuccessful = true
                            loginError = null
                            attemptsRemaining = null
                            isAccountLocked = false
                            lockRemainingTime = null

                            val accountId = cookieJar.getAccountId()
                            checkAndSaveInfoStatus(accountId)

                            if (!accountId.isNullOrEmpty()) {
                                val accountIdInt = accountId.toIntOrNull()
                                if (accountIdInt != null) {
                                    userRepository.getInfo(accountIdInt)
                                }
                            }
                            onSuccess()
                        }

                        // ❌ 401 - ĐĂNG NHẬP THẤT BẠI (Sai mật khẩu)
                        401 -> {
                            isLoginSuccessful = false
                            isAccountLocked = false

                            val errorBody = response.errorBody()?.string()
                            if (!errorBody.isNullOrEmpty()) {
                                try {
                                    val errorResponse = gson.fromJson(errorBody, LoginErrorResponse::class.java)
                                    loginError = errorResponse.message
                                    attemptsRemaining = errorResponse.attemptsRemaining

                                    // Hiển thị thông báo chi tiết
                                    val detailedMessage = if (errorResponse.attemptsRemaining != null) {
                                        "${errorResponse.message}\nCòn ${errorResponse.attemptsRemaining} lần thử."
                                    } else {
                                        errorResponse.message
                                    }
                                    onFail(detailedMessage)
                                } catch (e: Exception) {
                                    loginError = "Đăng nhập thất bại"
                                    onFail("Đăng nhập thất bại")
                                }
                            } else {
                                loginError = "Đăng nhập thất bại"
                                onFail("Đăng nhập thất bại")
                            }
                        }

                        // 🔒 403 - TÀI KHOẢN BỊ KHÓA
                        403 -> {
                            isLoginSuccessful = false
                            isAccountLocked = true

                            val errorBody = response.errorBody()?.string()
                            if (!errorBody.isNullOrEmpty()) {
                                try {
                                    val lockedResponse = gson.fromJson(errorBody, LoginLockedResponse::class.java)
                                    loginError = lockedResponse.message
                                    lockRemainingTime = lockedResponse.remainingTime

                                    onLocked(lockedResponse.message, lockedResponse.remainingTime)
                                } catch (e: Exception) {
                                    loginError = "Tài khoản bị khóa"
                                    onFail("Tài khoản bị khóa")
                                }
                            } else {
                                loginError = "Tài khoản bị khóa"
                                onFail("Tài khoản bị khóa")
                            }
                        }

                        // ⚠️ CÁC MÃ LỖI KHÁC
                        else -> {
                            isLoginSuccessful = false
                            val errorMessage = "Lỗi: ${response.code()} - ${response.message()}"
                            loginError = errorMessage
                            onFail(errorMessage)
                        }
                    }
                } catch (e: Exception) {
                    isLoginSuccessful = false
                    loginError = e.message ?: e.toString()
                    onFail(e.message ?: "Lỗi kết nối")
                }
            }
        }
    }

    suspend fun checkAndSaveInfoStatus(accountId: String?) {
        if (accountId.isNullOrEmpty()) {
            cookieJar.saveInfoUpdated(false)
            return
        }
        try {
            val res = userApi.check_info(accountId.toInt())
            cookieJar.saveInfoUpdated(res.success)
        } catch (e: Exception) {
            cookieJar.saveInfoUpdated(false)
        }
    }

    fun checkLoginStatus(callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            callback(auRepository.isLoggedIn())
        }
    }

    fun logout() {
        viewModelScope.launch {
            auRepository.logout()
            cookieJar.logout()
            reset()
        }
    }

    fun reset() {
        numberPhone = ""
        passWord = ""
        isLoginSuccessful = false
        phoneNumberError = null
        passwordError = null
        loginError = null
        attemptsRemaining = null
        isAccountLocked = false
        lockRemainingTime = null
    }

    // ✅ HELPER: Format thời gian còn lại
    fun formatRemainingTime(milliseconds: Long): String {
        val minutes = (milliseconds / 1000) / 60
        val seconds = (milliseconds / 1000) % 60
        return if (minutes > 0) {
            "${minutes} phút ${seconds} giây"
        } else {
            "${seconds} giây"
        }
    }
}