package com.example.dat_lich_kham_fe.util

import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import java.util.*

// CompositionLocal for current language
val LocalLanguage = compositionLocalOf { "vi" }

@Composable
fun LanguageProvider(
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val languageStore = remember { LanguageLocalStore(context) }

    var currentLanguage by remember {
        mutableStateOf(languageStore.getLanguage())
    }

    // Function to change language globally
    val changeLanguage = { newLanguage: String ->
        if (currentLanguage != newLanguage) {
            currentLanguage = newLanguage
            languageStore.saveLanguage(newLanguage)

            // Update system locale
            val locale = Locale(newLanguage)
            Locale.setDefault(locale)

            // Update context configuration
            val config = context.resources.configuration
            config.setLocale(locale)
            config.setLayoutDirection(locale)
        }
    }

    // Provide both current language and change function
    CompositionLocalProvider(
        LocalLanguage provides currentLanguage,
        LocalLanguageChanger provides changeLanguage
    ) {
        content()
    }
}

// CompositionLocal for language changer function
val LocalLanguageChanger = compositionLocalOf<(String) -> Unit> { {} }

@Composable
fun rememberLanguageController(): LanguageController {
    val context = LocalContext.current
    val languageStore = remember { LanguageLocalStore(context) }
    val currentLanguage = LocalLanguage.current
    val changeLanguage = LocalLanguageChanger.current

    return remember(currentLanguage, changeLanguage) {
        LanguageController(languageStore, currentLanguage, changeLanguage)
    }
}

class LanguageController(
    private val languageStore: LanguageLocalStore,
    private val currentLanguageValue: String,
    private val changeLanguageCallback: (String) -> Unit
) {
    val currentLanguage: String get() = currentLanguageValue

    fun setLanguage(languageCode: String) {
        changeLanguageCallback(languageCode)
    }

    fun isEnglish() = currentLanguageValue == "en"
    fun isVietnamese() = currentLanguageValue == "vi"
}
