package com.example.dao

import com.example.Tables.Transactions
import com.example.utils.toKotlinxInstant
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class TransactionsDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TransactionsDAO>(Transactions)

    var userId by Transactions.userId
    var category by Transactions.category
    var transactionType by Transactions.transactionType
    var amount by Transactions.amount
    var isIncome by Transactions.isIncome
    var transactionTime by Transactions.transactionTime

    fun toModel(): com.example.models.Transaction {
        return com.example.models.Transaction(
            id = id.value,
            userId = userId.value,
            category = category,
            transactionType = transactionType,
            amount = amount,
            isIncome = isIncome,
            transactionTime = transactionTime.toKotlinxInstant()
        )
    }
}