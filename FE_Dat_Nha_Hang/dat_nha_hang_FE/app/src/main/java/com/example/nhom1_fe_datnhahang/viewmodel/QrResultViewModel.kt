package com.example.nhom1_fe_datnhahang.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class QrResultViewModel : ViewModel() {
    var qrResult by mutableStateOf("")
        private set
    fun onQrResultChange(newResult: String) {
        qrResult = newResult
    }
}
