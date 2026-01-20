package viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import data.model.DepartmentResponse
import data.repository.DepartmentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Thêm sealed class để quản lý trạng thái tạo khoa
sealed class CreateDepartmentState {
    object Idle : CreateDepartmentState()
    object Loading : CreateDepartmentState()
    data class Success(val message: String) : CreateDepartmentState()
    data class Error(val message: String) : CreateDepartmentState()
}

class DerpartmentViewModel : ScreenModel {
    private val departmentRepository = DepartmentRepository()
    private val _department = MutableStateFlow<List<DepartmentResponse>?>(null)
    val departments: StateFlow<List<DepartmentResponse>?> = _department

    // Thêm StateFlow để quản lý trạng thái tạo khoa
    private val _createDepartmentState = MutableStateFlow<CreateDepartmentState>(CreateDepartmentState.Idle)
    val createDepartmentState: StateFlow<CreateDepartmentState> = _createDepartmentState

    fun fetchDepartment() {
        screenModelScope.launch(Dispatchers.Default) {
            try {
                _department.value = departmentRepository.listDepartment()
            }catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Thêm function để tạo khoa mới
    fun createDepartment(name: String, description: String) {
        screenModelScope.launch(Dispatchers.Default) {
            try {
                _createDepartmentState.value = CreateDepartmentState.Loading

                val response = departmentRepository.createDepartment(name, description)

                if (response.isSuccessful && response.body()?.success == true) {
                    _createDepartmentState.value = CreateDepartmentState.Success(
                        response.body()?.message ?: "Thêm khoa thành công"
                    )
                    // Refresh danh sách khoa sau khi thêm thành công
                    fetchDepartment()
                } else {
                    _createDepartmentState.value = CreateDepartmentState.Error(
                        response.body()?.message ?: "Thêm khoa thất bại"
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _createDepartmentState.value = CreateDepartmentState.Error(
                    e.message ?: "Có lỗi xảy ra khi thêm khoa"
                )
            }
        }
    }

    // Thêm function để reset state
    fun resetCreateDepartmentState() {
        _createDepartmentState.value = CreateDepartmentState.Idle
    }
}