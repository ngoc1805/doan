package viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import data.repository.AccountRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AccountViewModel: ScreenModel {
    private val accountRepository = AccountRepository()

    private val _createDoctorState = MutableStateFlow<CreateDoctorState>(CreateDoctorState.Idle)
    val createDoctorState: StateFlow<CreateDoctorState> = _createDoctorState

    private val _changePasswordState = MutableStateFlow(ChangePasswordUiState())
    val changePasswordState: StateFlow<ChangePasswordUiState> = _changePasswordState.asStateFlow()

    private val _createServiceRoomState = MutableStateFlow<CreateServiceRoomState>(CreateServiceRoomState.Idle)
    val createServiceRoomState: StateFlow<CreateServiceRoomState> = _createServiceRoomState

    fun createDoctor(name: String, code: String, departmentId: Int, examPrice: Int) {
        screenModelScope.launch(Dispatchers.Default) {
            _createDoctorState.value = CreateDoctorState.Loading
            try {
                val response = accountRepository.createDoctor(name, code, departmentId, examPrice)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.success == true) {
                        _createDoctorState.value = CreateDoctorState.Success(body.message)
                    } else {
                        _createDoctorState.value = CreateDoctorState.Error(body?.message ?: "Có lỗi xảy ra")
                    }
                } else {
                    _createDoctorState.value = CreateDoctorState.Error("Lỗi kết nối: ${response.code()}")
                }
            } catch (e: Exception) {
                _createDoctorState.value = CreateDoctorState.Error(e.message ?: "Lỗi không xác định")
            }
        }
    }
    fun changePassword(accountId: Int, oldPassword: String, newPassword: String) {
        screenModelScope.launch(Dispatchers.Default) {
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
    fun createServiceRoom(name: String, code: String, address: String, examPrice: Int) {
        screenModelScope.launch {
            _createServiceRoomState.value = CreateServiceRoomState.Loading
            try {
                val response = accountRepository.createServiceRoom(name, code, address, examPrice)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.success == true) {
                        _createServiceRoomState.value = CreateServiceRoomState.Success(body.message)
                    } else {
                        _createServiceRoomState.value = CreateServiceRoomState.Error(body?.message ?: "Có lỗi xảy ra")
                    }
                } else {
                    _createServiceRoomState.value = CreateServiceRoomState.Error("Lỗi kết nối: ${response.code()}")
                }
            } catch (e: Exception) {
                _createServiceRoomState.value = CreateServiceRoomState.Error(e.message ?: "Lỗi không xác định")
            }
        }
    }

    fun resetChangePasswordState() {
        _changePasswordState.value = ChangePasswordUiState()
    }

    fun resetState() {
        _createDoctorState.value = CreateDoctorState.Idle
    }
    fun resetServiceRoomState() {
        _createServiceRoomState.value = CreateServiceRoomState.Idle
    }
}

sealed class CreateDoctorState {
    object Idle : CreateDoctorState()
    object Loading : CreateDoctorState()
    data class Success(val message: String) : CreateDoctorState()
    data class Error(val message: String) : CreateDoctorState()
}
data class ChangePasswordUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)
sealed class CreateServiceRoomState {
    object Idle : CreateServiceRoomState()
    object Loading : CreateServiceRoomState()
    data class Success(val message: String) : CreateServiceRoomState()
    data class Error(val message: String) : CreateServiceRoomState()
}