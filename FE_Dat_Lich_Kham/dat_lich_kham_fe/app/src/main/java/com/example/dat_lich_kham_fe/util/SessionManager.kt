package com.example.dat_lich_kham_fe.util

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object SessionManager {
    private val _sessionExpired = MutableStateFlow(false)
    val sessionExpired: StateFlow<Boolean> = _sessionExpired

    fun triggerSessionExpired() {
        _sessionExpired.value = true
    }

    fun reset() {
        _sessionExpired.value = false
    }
}