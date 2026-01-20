package com.example.dat_lich_kham_fe.util

import android.content.Context
import android.content.SharedPreferences

class LanguageLocalStore(private val context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "language_prefs",
        Context.MODE_PRIVATE
    )

    companion object {
        private const val KEY_LANGUAGE = "selected_language"
        private const val DEFAULT_LANGUAGE = "vi" // Vietnamese as default
    }

    fun saveLanguage(languageCode: String) {
        prefs.edit().putString(KEY_LANGUAGE, languageCode).apply()
    }

    fun getLanguage(): String {
        return prefs.getString(KEY_LANGUAGE, DEFAULT_LANGUAGE) ?: DEFAULT_LANGUAGE
    }

    fun clearLanguage() {
        prefs.edit().remove(KEY_LANGUAGE).apply()
        // Reset to default language
        saveLanguage(DEFAULT_LANGUAGE)
        // Apply default language
        LocaleHelper.setLocale(context, DEFAULT_LANGUAGE)
    }

    fun isEnglish(): Boolean {
        return getLanguage() == "en"
    }

    fun isVietnamese(): Boolean {
        return getLanguage() == "vi"
    }

    // Initialize language on app start
    fun initializeLanguage() {
        val savedLanguage = getLanguage()
        LocaleHelper.setLocale(context, savedLanguage)
    }
}
