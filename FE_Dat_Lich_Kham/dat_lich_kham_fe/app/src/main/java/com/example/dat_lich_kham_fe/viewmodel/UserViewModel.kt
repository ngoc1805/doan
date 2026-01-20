package com.example.dat_lich_kham_fe.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dat_lich_kham_fe.data.model.CanteenResponse
import com.example.dat_lich_kham_fe.data.repository.UserRepository
import com.example.dat_lich_kham_fe.util.PersistentCookieJar
import com.example.dat_lich_kham_fe.util.UserLocalStore
import kotlinx.coroutines.launch

class UserViewModel(private val context: Context) : ViewModel() {
    private val userRepository = UserRepository(context)
    private val cookieJar = PersistentCookieJar(context)
    private val userLocalStore = UserLocalStore(context)
    val canteen = mutableStateOf<CanteenResponse?>(null)

    var balance by mutableStateOf(0)
        private set
    var pinStatus by mutableStateOf<Boolean?>(null)
        private set
    var pinCheckResult by mutableStateOf<Boolean?>(null)
        private set

    init {
        viewModelScope.launch {
            balance = userLocalStore.getBalance() ?: 0
        }
    }

    fun updatebalance(userId: Int, newBalance: Int) {
        viewModelScope.launch {
            try {
                val response = userRepository.updateBalance(userId, newBalance)
                if(response.isSuccessful){
                    val accountId = cookieJar.getAccountId()?.toIntOrNull()
                    if (accountId != null) {
                        userRepository.getInfo(accountId)
                        balance = userLocalStore.getBalance() ?: 0
                    }
                }
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error updating balance: ${e.message}")
            }
        }
    }
    fun updatebalance2() {
        viewModelScope.launch {
            try {
                    val accountId = cookieJar.getAccountId()?.toIntOrNull()
                    if (accountId != null) {
                        userRepository.getInfo(accountId)
                        balance = userLocalStore.getBalance() ?: 0
                    }
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error updating balance: ${e.message}")
            }
        }
    }

    // *** THÊM MỚI: Hàm lấy balance từ server ***
    fun getUserBalance(userId: Int, callback: (balance: Int?) -> Unit) {
        viewModelScope.launch {
            try {
                val accountId = cookieJar.getAccountId()?.toIntOrNull()
                if (accountId != null) {
                    val response = userRepository.getInfo(accountId)
                    if (response.isSuccessful) {
                        // Sau khi getInfo, balance đã được lưu vào local store
                        val newBalance = userLocalStore.getBalance() ?: 0
                        balance = newBalance
                        callback(newBalance)
                    } else {
                        callback(null)
                    }
                } else {
                    callback(null)
                }
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error getting user balance: ${e.message}")
                callback(null)
            }
        }
    }

    fun checkHasPin(userId: Int, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val res = userRepository.hasPin(userId)
                val success = res.body()?.success == true
                val data = res.body()?.data?.toString()?.toBoolean() ?: false
                pinStatus = data
                onResult(data)
            } catch (e: Exception) {
                pinStatus = false
                onResult(false)
            }
        }
    }

    fun createOrUpdatePin(userId: Int, pin: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val res = userRepository.updatePin(userId, pin)
                val success = res.body()?.success == true
                onResult(success)
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }

    fun checkPin(userId: Int, pin: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val res = userRepository.comparePin(userId, pin)
                val result = res.body()?.data?.toString()?.toBoolean() ?: false
                pinCheckResult = result
                onResult(result)
            } catch (e: Exception) {
                pinCheckResult = false
                onResult(false)
            }
        }
    }

    fun canteenInfo(){
        viewModelScope.launch {
            try {
                val response = userRepository.canteenInfo()
                if(response.isSuccessful){
                    canteen.value = response.body()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}