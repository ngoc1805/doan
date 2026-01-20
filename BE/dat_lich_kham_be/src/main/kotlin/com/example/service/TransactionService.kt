package com.example.service

import com.example.repository.TransactionRepository
import com.example.models.Transaction

class TransactionService(private val repository: TransactionRepository = TransactionRepository()) {
    fun getTransactionHistory(userId: Int): List<Transaction> {
        return repository.getTransactionHistoryByUserId(userId)
    }
}