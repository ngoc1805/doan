package viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import data.model.InpatientItem
import data.repository.InpatientRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class InpatientViewModel: ScreenModel {
    private val inpatientRepository = InpatientRepository()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    val _resultMessage = MutableStateFlow("")
    val resultMessage: StateFlow<String> = _resultMessage

    val _isSuccess = MutableStateFlow<Boolean?>(null)
    val isSuccess: StateFlow<Boolean?> = _isSuccess

    var inpatients by mutableStateOf<List<InpatientItem>>(emptyList())

    fun createInpatient(userId: Int, appointmentId: Int? = null) {
        screenModelScope.launch(Dispatchers.Default) {
            try {
                _isLoading.value = true
                _resultMessage.value = ""
                _isSuccess.value = null

                val response = inpatientRepository.creatInpatient(userId, appointmentId)

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        if (body.success) {
                            // Thành công - bệnh nhân được thêm vào nội trú
                            _isSuccess.value = true
                            _resultMessage.value = "Đã chuyển bệnh nhân vào điều trị nội trú thành công"
                        } else {
                            // Bệnh nhân đã có trong hệ thống nội trú
                            _isSuccess.value = false
                            _resultMessage.value = body.message ?: "Bệnh nhân đã nhập viện, không thể tạo mới."
                        }
                    } else {
                        _isSuccess.value = false
                        _resultMessage.value = "Không nhận được phản hồi từ server"
                    }
                } else {
                    _isSuccess.value = false
                    _resultMessage.value = "Lỗi kết nối: ${response.code()} - ${response.message()}"
                }
            } catch (e: Exception) {
                _isSuccess.value = false
                _resultMessage.value = "Lỗi: ${e.message ?: "Không xác định"}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchInpatient(status: String){
        screenModelScope.launch(Dispatchers.Default) {
            try {
                val response = inpatientRepository.getInpatient(status)
                if(response.isSuccessful){
                    inpatients = response.body()?.inpatients ?: emptyList()
                }else{

                }
            }catch (e : Exception){
                e.printStackTrace()
            }
        }
    }

    fun updateStatus(id: Int){
        screenModelScope.launch(Dispatchers.Default) {
            try {
                val response = inpatientRepository.updateStatus(id)
                if(response.isSuccessful){
                    // Tự động load lại dữ liệu sau khi update thành công
                    fetchInpatient("Đã nhập viện")
                }else{

                }
            }catch (e : Exception){
                e.printStackTrace()
            }
        }
    }

    fun updateAddress(id: Int, address: String){
        screenModelScope.launch(Dispatchers.Default) {
            try {
                val response = inpatientRepository.updateAddress(id, address)
                if(response.isSuccessful){
                    // Tự động load lại dữ liệu sau khi update thành công
                    fetchInpatient("Đang chờ")
                }else{

                }
            }catch (e : Exception){
                e.printStackTrace()
            }
        }
    }



    fun resetState() {
        _resultMessage.value = ""
        _isSuccess.value = null
    }
}