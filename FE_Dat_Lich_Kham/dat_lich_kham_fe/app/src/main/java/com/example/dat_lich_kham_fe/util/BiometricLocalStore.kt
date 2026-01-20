package com.example.dat_lich_kham_fe.util

import android.content.Context
import android.content.SharedPreferences

class BiometricLocalStore(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("BiometricPrefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"
        private const val KEY_BIOMETRIC_SETUP_DONE = "biometric_setup_done"
    }

    /**
     * Lưu trạng thái bật/tắt sinh trắc học
     */
    fun setBiometricEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_BIOMETRIC_ENABLED, enabled).apply()
    }

    /**
     * Kiểm tra sinh trắc học có được bật không
     */
    fun isBiometricEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_BIOMETRIC_ENABLED, false)
    }


    fun setBiometricSetupDone(done: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_BIOMETRIC_SETUP_DONE, done).apply()
    }


    fun isBiometricSetupDone(): Boolean {
        return sharedPreferences.getBoolean(KEY_BIOMETRIC_SETUP_DONE, false)
    }


    fun clearBiometricSettings() {
        sharedPreferences.edit()
            .remove(KEY_BIOMETRIC_ENABLED)
            .remove(KEY_BIOMETRIC_SETUP_DONE)
            .apply()
    }
}