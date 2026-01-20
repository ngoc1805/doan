package com.example.service

import com.example.Tables.Accounts
import com.example.dao.AccountDAO
import com.example.repository.AccountRepository
import org.jetbrains.exposed.sql.transactions.transaction

class AccountService(
    private val accountRepository: AccountRepository
) {

    fun updateFmcToken(accountId: Int, fmcToken: String): Boolean {
        return accountRepository.updateFmcToken(accountId, fmcToken)
    }

    fun createDoctor(name: String, code: String, departmentId: Int, examPrice: Int): Boolean {
        return accountRepository.createDoctorWithAccount(name, code, departmentId, examPrice)
    }

    fun createServiceRoom(name: String, code: String, address: String, examPrice: Int): Boolean {
        return accountRepository.createServiceRoomWithAccount(name, code, address, examPrice)
    }
    fun changePassword(accountId: Int, oldPassword: String?, newPassword: String): Boolean {
        return accountRepository.changePassword(accountId, oldPassword, newPassword)
    }


}