package com.example.nhom1_fe_datnhahang.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.nhom1_fe_datnhahang.data.api.RetrofitInstance
import com.example.nhom1_fe_datnhahang.data.repository.AuthRepository
import com.example.nhom1_fe_datnhahang.data.repository.UserRepository
import com.example.nhom1_fe_datnhahang.util.PersistentCookieJar
import kotlinx.coroutines.launch

class LoginViewModel(private val context: Context): ViewModel() {
    private val cookieJar = PersistentCookieJar(context)
    private val authRepository = AuthRepository(context)
    private val userApi = RetrofitInstance.userApi(context)
    private val userRepository = UserRepository(context)

    var userName by mutableStateOf("")
        private set
    var passWord by mutableStateOf("")
        private set
    var isLoading by mutableStateOf(false)
        private set

    var userNameError by mutableStateOf<String?>(null)
        private set

    var passwordError by mutableStateOf<String?>(null)
        private set

    var loginError by mutableStateOf<String?>(null)
        private set

    fun onUserNameChanged(newUserName: String) {
        userName = newUserName
        userNameError = null
        loginError = null
    }

    fun onPassWordChanged(newPassWord: String) {
        passWord = newPassWord
        passwordError = null
        loginError = null
    }

    fun validateAndLogin(onSuccess: () -> Unit, onFail: (String) -> Unit) {
        // Validate input
        val isUserNameNotEmpty = userName.isNotEmpty()
        val isPasswordNotEmpty = passWord.isNotEmpty()

        userNameError = when {
            !isUserNameNotEmpty -> "Vui lòng nhập tên đăng nhập"
            else -> null
        }
        passwordError = when {
            !isPasswordNotEmpty -> "Vui lòng nhập mật khẩu"
            else -> null
        }

        if (!isUserNameNotEmpty || !isPasswordNotEmpty) {
            return
        }

        // Proceed with login
        viewModelScope.launch {
            try {
                isLoading = true
                loginError = null

                val response = authRepository.login(userName, passWord)

                if (response.isSuccessful) {
                    // Token đã được lưu tự động qua CookieJar
                    // Kiểm tra role
                    val role = cookieJar.getRole()
                    val accountId = cookieJar.getaccountId()

                    if (role == "nhaan") {
                        // Role đúng, đăng nhập thành công
                        if (!accountId.isNullOrEmpty()) {
                            val accountIdInt = accountId.toIntOrNull()
                            if (accountIdInt != null) {
                                userRepository.getInfo(accountIdInt)
                            }
                        }
                        onSuccess()
                    } else {
                        // Role sai, xóa token và báo lỗi
                        cookieJar.logout()
                        val errorMsg = "Tài khoản không có quyền truy cập"
                        loginError = errorMsg
                        onFail(errorMsg)
                    }
                } else {
                    val errorMsg = response.body()?.message ?: "Sai tên đăng nhập hoặc mật khẩu"
                    loginError = errorMsg
                    onFail(errorMsg)
                }
            } catch (e: Exception) {
                val errorMsg = "Lỗi kết nối: ${e.message}"
                loginError = errorMsg
                onFail(errorMsg)
            } finally {
                isLoading = false
            }
        }
    }

    fun checkLoginStatus(onLoggedIn: () -> Unit, onNotLoggedIn: () -> Unit) {
        viewModelScope.launch {
            try {
                val hasToken = cookieJar.hasToken()
                val isLoggedIn = cookieJar.isLoggedIn()

                if (hasToken && isLoggedIn) {
                    // Kiểm tra role
                    val role = cookieJar.getRole()
                    if (role == "nhaan") {
                        onLoggedIn()
                    } else {
                        // Token có nhưng role không đúng, xóa và đăng xuất
                        cookieJar.logout()
                        onNotLoggedIn()
                    }
                } else {
                    onNotLoggedIn()
                }
            } catch (e: Exception) {
                onNotLoggedIn()
            }
        }
    }
}

class LoginViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
