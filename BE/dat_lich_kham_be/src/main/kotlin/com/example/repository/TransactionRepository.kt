package com.example.repository

import com.example.Tables.Transactions
import com.example.Tables.Users
import com.example.dao.TransactionsDAO
import com.example.utils.EncryptionUtil
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

class TransactionRepository {

    fun addTransaction(
        userId: Int,
        category: String,
        transactionType: String,
        amount: Long,
        isIncome: Boolean
    ): Int? = transaction {
        try {
            // Mã hóa transactionType
            val encryptedType = EncryptionUtil.encryptTransaction(transactionType)
                ?: throw Exception("Không thể mã hóa transactionType")

            // Format amount với dấu + hoặc -
            val formattedAmount = if (isIncome) "+$amount" else "-$amount"

            // Tạo bản ghi mới
            val newTransaction = TransactionsDAO.new {
                this.userId = EntityID(userId, Users)
                this.category = category
                this.transactionType = encryptedType
                this.amount = formattedAmount
                this.isIncome = isIncome
            }

            println("✓ Đã thêm giao dịch: userId=$userId, category=$category, amount=$formattedAmount")
            newTransaction.id.value
        } catch (e: Exception) {
            println("✗ Lỗi khi thêm giao dịch: ${e.message}")
            e.printStackTrace()
            null
        }
    }

    fun addMoMoDepositTransaction(userId: Int, amount: Long): Int? {
        return addTransaction(
            userId = userId,
            category = "Nạp tiền",
            transactionType = "Nạp tiền từ MoMo",
            amount = amount,
            isIncome = true
        )
    }

    //
    fun getTransactionHistoryByUserId(userId: Int): List<com.example.models.Transaction> = transaction {
        try {
            TransactionsDAO.find { com.example.Tables.Transactions.userId eq userId }
//                .orderBy(Transactions.transactionTime, false)
                .map { dao ->
                    val decryptedType = com.example.utils.EncryptionUtil.decryptTransaction(dao.transactionType) ?: ""
                    dao.toModel().copy(transactionType = decryptedType)
                }
        } catch (e: Exception) {
            println("Lỗi lấy lịch sử giao dịch: ${e.message}")
            emptyList()
        }
    }
}