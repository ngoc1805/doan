package com.example.dat_lich_kham_fe.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dat_lich_kham_fe.data.repository.InpatientRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class InpatientViewModel(private val context: Context) : ViewModel() {
    private val inpatientRepository = InpatientRepository(context)

    private val _admittedResult = MutableStateFlow<Boolean?>(null)
    val admittedResult: StateFlow<Boolean?> get() = _admittedResult

    private val _addressResult = MutableStateFlow<String?>(null)
    val addressResult: StateFlow<String?> get() = _addressResult

    private val _currentInpatient = MutableStateFlow<com.example.dat_lich_kham_fe.data.model.InpatientItem?>(null)
    val currentInpatient: StateFlow<com.example.dat_lich_kham_fe.data.model.InpatientItem?> get() = _currentInpatient

    // Add missing inpatientHistory StateFlow
    private val _inpatientHistory = MutableStateFlow<List<com.example.dat_lich_kham_fe.data.model.InpatientItem>>(emptyList())
    val inpatientHistory: StateFlow<List<com.example.dat_lich_kham_fe.data.model.InpatientItem>> get() = _inpatientHistory

    fun checkAdmitted(userId: Int) {
        viewModelScope.launch {
            val result = inpatientRepository.checkAdmitted(userId)
            _admittedResult.value = result
        }
    }

    fun getAddress(userId: Int) {
        viewModelScope.launch {
            val result = inpatientRepository.getAddress(userId)
            _addressResult.value = result
        }
    }

    fun getCurrentInpatient(userId: Int) {
        viewModelScope.launch {
            val result = inpatientRepository.getCurrentInpatient(userId)
            _currentInpatient.value = result
        }
    }

    // Add missing getInpatientHistory function
    fun getInpatientHistory(userId: Int) {
        viewModelScope.launch {
            val result = inpatientRepository.getInpatientHistory(userId)
            _inpatientHistory.value = result ?: emptyList()
        }
    }
}