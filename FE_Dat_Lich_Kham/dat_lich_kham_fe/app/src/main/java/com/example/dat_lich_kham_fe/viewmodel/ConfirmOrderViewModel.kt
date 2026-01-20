package com.example.dat_lich_kham_fe.viewmodel

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dat_lich_kham_fe.util.PersistentCookieJar
import com.example.dat_lich_kham_fe.util.UserLocalStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ConfirmOrderViewModel(private val context: Context) : ViewModel() {
    val orderViewModel = OrderViewModel(context)
    val orderItemViewModel = OrderItemViewModel(context)

    var note by mutableStateOf("")
        private set
    var address by mutableStateOf("")
        private set

    fun onNoteChange(newNote: String) {
        note = newNote
    }
    fun onAddressChange(newAddress: String) {
        address = newAddress
    }

    val userStore = UserLocalStore(context)
    @RequiresApi(Build.VERSION_CODES.O)
    fun handleConfirmOrder(){
        CoroutineScope(Dispatchers.Main).launch {
            val user = userStore.getUser()
            val userId = user?.Id ?: 0
            val phone = PersistentCookieJar(context).getUsername().toString()
            val orderId = orderViewModel.createOrder(
                userId = userId,
                phone = phone,
                address = address,
                note = note,
                status = "Đã đặt hàng"
            )
            if(orderId !=null){

            }
        }
    }
}
