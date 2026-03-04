package com.example.dat_lich_kham_fe.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dat_lich_kham_fe.data.api.RetrofitInstance
import com.example.dat_lich_kham_fe.data.model.MealStatisticsResponse
import com.example.dat_lich_kham_fe.data.model.MealStatusResponse
import com.example.dat_lich_kham_fe.data.model.MealSubscriptionCycle
import com.example.dat_lich_kham_fe.data.repository.MealRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MealViewModel(context: Context) : ViewModel() {
    private val mealApi = RetrofitInstance.getMealApi(context)
    private val mealRepository = MealRepository(mealApi)

    private val _mealStatus = MutableStateFlow<MealStatusResponse?>(null)
    val mealStatus: StateFlow<MealStatusResponse?> = _mealStatus

    private val _mealHistory = MutableStateFlow<List<MealSubscriptionCycle>>(emptyList())
    val mealHistory: StateFlow<List<MealSubscriptionCycle>> = _mealHistory

    private val _mealStatistics = MutableStateFlow<MealStatisticsResponse?>(null)
    val mealStatistics: StateFlow<MealStatisticsResponse?> = _mealStatistics

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage

    private val _pendingCycleId = MutableStateFlow<Int?>(null)
    val pendingCycleId: StateFlow<Int?> = _pendingCycleId

    // Register meal
    fun registerMeal(inpatientId: Int, onSuccess: (Int) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = mealRepository.registerMeal(inpatientId)
                if (response.isSuccessful && response.body()?.success == true) {
                    // Parse cycleId from response data
                    val cycleId = try {
                        val data = response.body()?.data
                        // data is a JsonElement (Gson), need to convert to JsonObject
                        if (data != null && data.isJsonObject) {
                            val jsonObject = data.asJsonObject
                            jsonObject.get("cycleId")?.asInt
                        } else {
                            null
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        null
                    }
                    
                    if (cycleId != null) {
                        _pendingCycleId.value = cycleId
                        onSuccess(cycleId)
                    } else {
                        _error.value = "Không lấy được thông tin đăng ký"
                    }
                } else {
                    _error.value = response.body()?.message ?: "Đăng ký thất bại"
                }
            } catch (e: Exception) {
                _error.value = "Lỗi: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Renew meal
    fun renewMeal(inpatientId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = mealRepository.renewMeal(inpatientId)
                if (response.isSuccessful && response.body()?.success == true) {
                    _successMessage.value = response.body()?.message ?: "Gia hạn thành công"
                    // Refresh status
                    fetchMealStatus(inpatientId)
                } else {
                    _error.value = response.body()?.message ?: "Gia hạn thất bại"
                }
            } catch (e: Exception) {
                _error.value = "Lỗi: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Skip meal today
    fun skipMeal(inpatientId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val today = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
                } else {
                    java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
                }
                
                val response = mealRepository.skipMeal(inpatientId, today)
                if (response.isSuccessful && response.body()?.success == true) {
                    _successMessage.value = response.body()?.message ?: "Đã cắt cơm hôm nay"
                    // Refresh status
                    fetchMealStatus(inpatientId)
                } else {
                    _error.value = response.body()?.message ?: "Không thể cắt cơm"
                }
            } catch (e: Exception) {
                _error.value = "Lỗi: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Fetch meal status
    fun fetchMealStatus(inpatientId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = mealRepository.getMealStatus(inpatientId)
                if (response.isSuccessful) {
                    _mealStatus.value = response.body()
                } else {
                    _error.value = "Không thể tải trạng thái"
                }
            } catch (e: Exception) {
                _error.value = "Lỗi: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Fetch meal history
    fun fetchMealHistory(inpatientId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = mealRepository.getMealHistory(inpatientId)
                if (response.isSuccessful) {
                    _mealHistory.value = response.body()?.cycles ?: emptyList()
                }
            } catch (e: Exception) {
                _error.value = "Lỗi: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Fetch meal statistics
    fun fetchMealStatistics(inpatientId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = mealRepository.getMealStatistics(inpatientId)
                if (response.isSuccessful) {
                    _mealStatistics.value = response.body()
                }
            } catch (e: Exception) {
                _error.value = "Lỗi: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearMessages() {
        _error.value = null
        _successMessage.value = null
    }
}
