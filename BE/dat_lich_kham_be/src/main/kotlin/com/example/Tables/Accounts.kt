package com.example.Tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*

object Accounts : IntIdTable("account") {
    val username = varchar("username", 20)
    val password = varchar("password", 255)
    val twoFaKey = varchar("two_fa_key", 255).nullable()
    val apiKey = varchar("api_key", 255).nullable()
    val roleId = reference("roleid", Roles, onDelete = ReferenceOption.CASCADE).nullable()
    val enabled = byte("enabled").default(1)
    val confirm = byte("confirm").default(0)
    val fmctoken = varchar("fmc_token", 255)

    //  THÊM CÁC CỘT BẢO MẬT
    val loginAttempts = integer("login_attempts").default(0)
    val lastLoginAttempt = long("last_login_attempt").nullable()
    val lockedUntil = long("locked_until").nullable()
}