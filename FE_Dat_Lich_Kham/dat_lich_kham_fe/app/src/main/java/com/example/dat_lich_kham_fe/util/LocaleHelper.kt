package com.example.dat_lich_kham_fe.util

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import java.util.*

object LocaleHelper {

    fun setLocale(context: Context, languageCode: String): Context {
        return updateResources(context, languageCode)
    }

    private fun updateResources(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)

        return context.createConfigurationContext(configuration)
    }

    fun applyLanguageToActivity(activity: Activity, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration()
        config.locale = locale

        activity.resources.updateConfiguration(config, activity.resources.displayMetrics)

        // Recreate activity to apply changes
        activity.recreate()
    }

    fun getCurrentLocale(context: Context): String {
        return context.resources.configuration.locales[0].language
    }

    fun isRTL(context: Context): Boolean {
        val config = context.resources.configuration
        return config.layoutDirection == android.view.View.LAYOUT_DIRECTION_RTL
    }
}
