package com.example.dao

import com.example.Tables.Accounts
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class AccountDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<AccountDAO>(Accounts)

    var username by Accounts.username
    var password by Accounts.password
    var twoFaKey by Accounts.twoFaKey
    var apiKey by Accounts.apiKey
    var roleId by Accounts.roleId
    var enabled by Accounts.enabled
    var confirm by Accounts.confirm
    var fmctoken by Accounts.fmctoken

    //  THÊM CÁC TRƯỜNG BẢO MẬT
    var loginAttempts by Accounts.loginAttempts
    var lastLoginAttempt by Accounts.lastLoginAttempt
    var lockedUntil by Accounts.lockedUntil
}