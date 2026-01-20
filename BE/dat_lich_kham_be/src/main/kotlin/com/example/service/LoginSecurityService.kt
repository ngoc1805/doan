package com.example.service

import com.example.Tables.Accounts
import com.example.dao.AccountDAO
import org.jetbrains.exposed.sql.transactions.transaction

class LoginSecurityService {

    companion object {
        private const val MAX_ATTEMPTS = 5          // Số lần đăng nhập sai tối đa
        private const val LOCKOUT_DURATION = 15 * 60 * 1000L  // Khóa 15 phút
        private const val ATTEMPT_RESET_TIME = 5 * 60 * 1000L // Reset sau 5 phút không thử
    }

    /**
     * Kiểm tra tài khoản có bị khóa không
     * @return Pair<Boolean, Long> - (có bị khóa?, thời gian còn lại ms)
     */
    suspend fun isAccountLocked(username: String): Pair<Boolean, Long> {
        return transaction {
            val account = AccountDAO.find { Accounts.username eq username }.firstOrNull()

            if (account == null) {
                return@transaction Pair(false, 0L)
            }

            val lockedUntil = account.lockedUntil ?: 0L
            val now = System.currentTimeMillis()

            if (lockedUntil > now) {
                val remainingTime = lockedUntil - now
                Pair(true, remainingTime)
            } else {
                // Hết thời gian khóa -> reset
                if (lockedUntil > 0) {
                    account.loginAttempts = 0
                    account.lastLoginAttempt = null
                    account.lockedUntil = null
                }
                Pair(false, 0L)
            }
        }
    }

    /**
     * Ghi nhận lần đăng nhập thất bại
     * @return LoginSecurityResult
     */
    suspend fun recordFailedAttempt(username: String): LoginSecurityResult {
        return transaction {
            val account = AccountDAO.find { Accounts.username eq username }.firstOrNull()

            if (account == null) {
                return@transaction LoginSecurityResult.InvalidUsername
            }

            val now = System.currentTimeMillis()
            val lastAttemptTime = account.lastLoginAttempt ?: 0L
            val currentAttempts = account.loginAttempts

            // Reset nếu đã lâu không thử đăng nhập
            val newAttempts = if (now - lastAttemptTime > ATTEMPT_RESET_TIME) {
                1
            } else {
                currentAttempts + 1
            }

            // Cập nhật số lần thử
            account.loginAttempts = newAttempts
            account.lastLoginAttempt = now

            // Kiểm tra có cần khóa tài khoản không
            if (newAttempts >= MAX_ATTEMPTS) {
                val lockoutUntil = now + LOCKOUT_DURATION
                account.lockedUntil = lockoutUntil

                LoginSecurityResult.AccountLocked(
                    remainingTime = LOCKOUT_DURATION,
                    attemptsUsed = newAttempts
                )
            } else {
                val remainingAttempts = MAX_ATTEMPTS - newAttempts

                LoginSecurityResult.FailedAttempt(
                    attemptsUsed = newAttempts,
                    remainingAttempts = remainingAttempts
                )
            }
        }
    }

    /**
     * Ghi nhận đăng nhập thành công -> reset tất cả
     */
    suspend fun recordSuccessfulLogin(username: String) {
        transaction {
            val account = AccountDAO.find { Accounts.username eq username }.firstOrNull()
            account?.let {
                it.loginAttempts = 0
                it.lastLoginAttempt = null
                it.lockedUntil = null
            }
        }
    }

    /**
     * Lấy số lần đã thử đăng nhập
     */
    suspend fun getLoginAttempts(username: String): Int {
        return transaction {
            val account = AccountDAO.find { Accounts.username eq username }.firstOrNull()
            account?.loginAttempts ?: 0
        }
    }

    /**
     * Format thời gian còn lại
     */
    fun formatRemainingTime(milliseconds: Long): String {
        val minutes = (milliseconds / 1000) / 60
        val seconds = (milliseconds / 1000) % 60
        return if (minutes > 0) {
            "${minutes} phút ${seconds} giây"
        } else {
            "${seconds} giây"
        }
    }
}

/**
 * Kết quả kiểm tra bảo mật
 */
sealed class LoginSecurityResult {
    data class FailedAttempt(
        val attemptsUsed: Int,
        val remainingAttempts: Int
    ) : LoginSecurityResult()

    data class AccountLocked(
        val remainingTime: Long,
        val attemptsUsed: Int
    ) : LoginSecurityResult()

    object InvalidUsername : LoginSecurityResult()
}