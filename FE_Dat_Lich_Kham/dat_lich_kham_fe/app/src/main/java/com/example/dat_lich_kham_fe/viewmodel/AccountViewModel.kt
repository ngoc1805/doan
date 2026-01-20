package com.example.dat_lich_kham_fe.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dat_lich_kham_fe.data.repository.AccountRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChangePasswordUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class AccountViewModel(private val context: Context) : ViewModel() {
    private val accountRepository = AccountRepository(context)

    private val _changePasswordState = MutableStateFlow(ChangePasswordUiState())
    val changePasswordState: StateFlow<ChangePasswordUiState> = _changePasswordState.asStateFlow()

    fun updateFcmToken(accountId: Int, fmcToken: String) {
        viewModelScope.launch {
            try {
                val response = accountRepository.updateFmcToken(accountId, fmcToken)
                if(response.isSuccessful){
                    val responseBody = response.body()
                } else {
                    // Xử lý lỗi từ máy chủ
                }
            } catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    fun changePassword(accountId: Int, oldPassword: String, newPassword: String) {
        viewModelScope.launch {
            _changePasswordState.value = ChangePasswordUiState(isLoading = true)

            try {
                val response = accountRepository.changePassword(accountId, oldPassword, newPassword)

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody?.success == true) {
                        _changePasswordState.value = ChangePasswordUiState(
                            isSuccess = true,
                            successMessage = responseBody.message
                        )
                    } else {
                        _changePasswordState.value = ChangePasswordUiState(
                            errorMessage = responseBody?.message ?: "Đổi mật khẩu thất bại"
                        )
                    }
                } else {
                    _changePasswordState.value = ChangePasswordUiState(
                        errorMessage = "Lỗi: ${response.code()} - ${response.message()}"
                    )
                }
            } catch (e: Exception) {
                _changePasswordState.value = ChangePasswordUiState(
                    errorMessage = "Có lỗi xảy ra: ${e.message}"
                )
                e.printStackTrace()
            }
        }
    }

    fun resetChangePasswordState() {
        _changePasswordState.value = ChangePasswordUiState()
    }
}

class AccountViewModelFactory(private val context: Context) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AccountViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AccountViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
