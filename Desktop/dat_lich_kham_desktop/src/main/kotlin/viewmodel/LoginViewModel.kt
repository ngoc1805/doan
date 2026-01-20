package viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import data.repository.LoginRepository
import data.model.LoginResponse
import data.model.AccountInfo
import kotlinx.coroutines.Dispatchers

class LoginViewModel : ScreenModel {
    private val loginRepository = LoginRepository()

    private val _loginState = MutableStateFlow<LoginUiState>(LoginUiState.Initial)
    val loginState: StateFlow<LoginUiState> = _loginState

    fun login(username: String, password: String, selectedType: String) {
        screenModelScope.launch(Dispatchers.Default) {
            println("\n=== [VM-1] LOGIN VIEWMODEL START ===")
            println("[VM-2] Username: $username")
            println("[VM-3] Password: ${password.take(3)}***")
            println("[VM-4] Selected type: $selectedType")

            val response: LoginResponse = loginRepository.login(username, password)
            println("[VM-5] Login response: username=${response.username}, message=${response.message}")

            val token = loginRepository.getAccessToken()
            println("[VM-6] Token exists: ${token != null}")
            if (token != null) {
                println("[VM-7] Token preview: ${token.take(50)}...")
            } else {
                println("[VM-7] Token is NULL!")
            }

            val info = loginRepository.getUserInfo()
            println("[VM-8] User info: $info")

            val expectedRole = when (selectedType) {
                "Admin" -> "admin"
                "Bác sĩ" -> "bacsi"
                "Phòng chức năng" -> "chucnang"
                else -> ""
            }
            println("[VM-9] Expected role: '$expectedRole'")

            val validRoles = listOf("admin", "bacsi", "chucnang")

            if (token != null && info != null) {
                println("[VM-10] ✅ Token and info both exist")
                println("[VM-11] User role from info: '${info.role}'")
                println("[VM-12] Role comparison: '${info.role}' == '$expectedRole' ? ${info.role == expectedRole}")

                if (info.role == expectedRole) {
                    println("[VM-13] ✅ ROLE MATCHES! Setting Success state")
                    _loginState.value = LoginUiState.Success(info.role)
                } else if (!validRoles.contains(info.role)) {
                    println("[VM-14] ❌ Invalid role '${info.role}', logging out (not in valid roles)")
                    loginRepository.logout()
                    _loginState.value = LoginUiState.Error("Tài khoản hoặc mật khẩu không đúng (role không hợp lệ)")
                } else {
                    println("[VM-15] ❌ Role mismatch, logging out")
                    println("[VM-16]    Expected: '$expectedRole'")
                    println("[VM-17]    Got: '${info.role}'")
                    println("[VM-18]    Valid roles: $validRoles")
                    loginRepository.logout()
                    _loginState.value = LoginUiState.Error("Vui lòng chọn đúng loại tài khoản")
                }
            } else {
                println("[VM-19] ❌ Token or info is NULL, logging out")
                println("[VM-20]    Token null? ${token == null}")
                println("[VM-21]    Info null? ${info == null}")
                loginRepository.logout()
                _loginState.value = LoginUiState.Error("Tài khoản hoặc mật khẩu không đúng")
            }

            println("=== [VM-22] LOGIN VIEWMODEL END ===\n")
        }
    }

    fun resetState() {
        _loginState.value = LoginUiState.Initial
    }
}

sealed class LoginUiState {
    object Initial : LoginUiState()
    data class Success(val role: String) : LoginUiState() // truyền role
    data class Error(val message: String) : LoginUiState()
}