package com.example.dat_lich_kham_fe.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dat_lich_kham_fe.data.model.Transaction
import com.example.dat_lich_kham_fe.data.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TransactionUiState(
    val isLoading: Boolean = false,
    val transactions: List<Transaction> = emptyList(),
    val errorMessage: String? = null
)

class TransactionViewModel(context: Context) : ViewModel() {
    private val repository = TransactionRepository(context)

    private val _uiState = MutableStateFlow(TransactionUiState())
    val uiState: StateFlow<TransactionUiState> = _uiState.asStateFlow()

    fun loadTransactionHistory(userId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            repository.getTransactionHistory(userId).fold(
                onSuccess = { transactions ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        transactions = transactions.sortedByDescending { it.transactionTime }
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Đã xảy ra lỗi"
                    )
                }
            )
        }
    }
}