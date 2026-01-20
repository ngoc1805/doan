package com.example.nhom1_fe_datnhahang.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nhom1_fe_datnhahang.data.repository.UserRepository
import com.example.nhom1_fe_datnhahang.util.PersistentCookieJar
import com.example.nhom1_fe_datnhahang.util.UserLocalStore
import kotlinx.coroutines.launch

class UserViewModel(private val context: Context) : ViewModel() {
    private val userRepository = UserRepository(context)
    private val cookieJar = PersistentCookieJar(context)
    private val userLocalStore = UserLocalStore(context)
    var balance by mutableStateOf(0)
        private set
    init {
        viewModelScope.launch {
            val accountId = cookieJar.getaccountId()?.toIntOrNull()
            if (accountId != null) {
                userRepository.getInfo(accountId)
                balance = userLocalStore.getBalance() ?: 0
            }
        }
    }
    fun getBalance() {
        viewModelScope.launch {
            val accountId = cookieJar.getaccountId()?.toIntOrNull()
            if (accountId != null) {
                userRepository.getInfo(accountId)
                balance = userLocalStore.getBalance() ?: 0
            }
        }
    }
}
