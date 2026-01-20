package com.example.service

import com.example.dto.Request.UserRequest
import com.example.dto.Response.UserResponse
import com.example.repository.UserRepository

class UserService(
    private val userRepository: UserRepository
) {
    fun updateUserInfo(request: UserRequest): Boolean {
        return userRepository.updateOrCreateUserInfo(request)
    }
    fun isUserInfoComplete(accountId: Int): Boolean {
        return userRepository.isUserInfoComplete(accountId)
    }
    fun getUserByAccountId(accountId: Int): UserResponse? {
        return userRepository.getUserByAccountId(accountId)
    }
    fun updateBalanceByUserId(userId: Int, balance: Int): Boolean {
        return userRepository.updateBalanceByUserId(userId, balance)
    }
    fun hasPin(userId: Int): Boolean {
        return userRepository.hasPin(userId)
    }
    fun updatePin(userId: Int, pinCode: String): Boolean {
        return userRepository.updatePin(userId, pinCode)
    }
    fun comparePin(userId: Int, pinCode: String): Boolean {
        return userRepository.comparePin(userId, pinCode)
    }
    fun resetPinWithToken(accountId: Int, newPin: String): Boolean {
        return userRepository.resetPinWithToken(accountId, newPin)
    }

    fun changePinWithOldPin(userId: Int, oldPin: String, newPin: String): Boolean {
        return userRepository.changePinWithOldPin(userId, oldPin, newPin)
    }
}