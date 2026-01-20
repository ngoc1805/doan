package viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import data.repository.ResultRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ResultViewModel: ScreenModel {
    private val resultRepository = ResultRepository()

    // State cho UI
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _resultMessage = MutableStateFlow("")
    val resultMessage: StateFlow<String> = _resultMessage

    private val _isSuccess = MutableStateFlow<Boolean?>(null)
    val isSuccess: StateFlow<Boolean?> = _isSuccess

    fun createResult(appointmentId: Int, comment: String) {
        // Kiểm tra validation
        if (comment.trim().isEmpty()) {
            _resultMessage.value = "Vui lòng nhập nhận xét cho kết quả khám"
            _isSuccess.value = false
            return
        }

        if (appointmentId <= 0) {
            _resultMessage.value = "ID cuộc hẹn không hợp lệ"
            _isSuccess.value = false
            return
        }

        screenModelScope.launch(Dispatchers.Default) {
            try {
                _isLoading.value = true
                _resultMessage.value = "Đang tạo kết quả khám..."

                val response = resultRepository.createResult(appointmentId, comment.trim())

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody?.success == true) {
                        _resultMessage.value = responseBody.message
                        _isSuccess.value = true
                    } else {
                        _resultMessage.value = responseBody?.message ?: "Có lỗi xảy ra khi tạo kết quả"
                        _isSuccess.value = false
                    }
                } else {
                    val errorMessage = when (response.code()) {
                        400 -> "Dữ liệu không hợp lệ"
                        401 -> "Bạn không có quyền thực hiện thao tác này"
                        404 -> "Không tìm thấy cuộc hẹn"
                        500 -> "Lỗi server, vui lòng thử lại sau"
                        else -> "Lỗi kết nối (${response.code()})"
                    }
                    _resultMessage.value = errorMessage
                    _isSuccess.value = false
                }
            } catch (e: Exception) {
                _resultMessage.value = "Lỗi kết nối: ${e.message ?: "Không thể kết nối đến server"}"
                _isSuccess.value = false
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Reset state khi cần thiết
    fun resetState() {
        _resultMessage.value = ""
        _isSuccess.value = null
        _isLoading.value = false
    }
}