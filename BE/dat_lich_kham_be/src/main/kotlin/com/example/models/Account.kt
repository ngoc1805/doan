package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class Account(
    val id: Int,
    val username: String,
    val roleId: Int? = null,
    val enabled: Byte = 1,
    val confirm: Byte = 0,
    val role: String? = null,
    val fmctoken: String? = null,

    // ✅ THÊM CÁC TRƯỜNG BẢO MẬT
    val loginAttempts: Int = 0,
    val lastLoginAttempt: Long? = null,
    val lockedUntil: Long? = null
)