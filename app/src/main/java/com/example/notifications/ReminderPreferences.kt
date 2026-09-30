package com.example.notifications

import android.content.Context
import android.content.SharedPreferences

/**
 * Persists local prayer reminder preferences without requiring any backend.
 */
class ReminderPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "mantramaya_reminder_prefs"
        private const val KEY_MORNING_ENABLED = "morning_reminder_enabled"
        private const val KEY_MORNING_HOUR = "morning_reminder_hour"
        private const val KEY_MORNING_MINUTE = "morning_reminder_minute"

        private const val KEY_EVENING_ENABLED = "evening_reminder_enabled"
        private const val KEY_EVENING_HOUR = "evening_reminder_hour"
        private const val KEY_EVENING_MINUTE = "evening_reminder_minute"

        private const val KEY_SPECIAL_ENABLED = "special_reminder_enabled"
        private const val KEY_INITIAL_SETUP_DONE = "initial_setup_done"
        private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
        private const val KEY_USER_LANGUAGE = "user_language"
        private const val KEY_LANGUAGE_SELECTED = "language_selected"
    }

    var isOnboardingCompleted: Boolean
        get() = prefs.getBoolean(KEY_ONBOARDING_COMPLETED, false)
        set(value) = prefs.edit().putBoolean(KEY_ONBOARDING_COMPLETED, value).apply()

    var userLanguage: String
        get() = prefs.getString(KEY_USER_LANGUAGE, "mr") ?: "mr"
        set(value) = prefs.edit().putString(KEY_USER_LANGUAGE, value).apply()

    var isLanguageSelected: Boolean
        get() = prefs.getBoolean(KEY_LANGUAGE_SELECTED, false)
        set(value) = prefs.edit().putBoolean(KEY_LANGUAGE_SELECTED, value).apply()

    var isMorningEnabled: Boolean
        get() = prefs.getBoolean(KEY_MORNING_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_MORNING_ENABLED, value).apply()

    var morningHour: Int
        get() = prefs.getInt(KEY_MORNING_HOUR, 6)
        set(value) = prefs.edit().putInt(KEY_MORNING_HOUR, value).apply()

    var morningMinute: Int
        get() = prefs.getInt(KEY_MORNING_MINUTE, 0)
        set(value) = prefs.edit().putInt(KEY_MORNING_MINUTE, value).apply()

    var isEveningEnabled: Boolean
        get() = prefs.getBoolean(KEY_EVENING_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_EVENING_ENABLED, value).apply()

    var eveningHour: Int
        get() = prefs.getInt(KEY_EVENING_HOUR, 19) // 7:00 PM
        set(value) = prefs.edit().putInt(KEY_EVENING_HOUR, value).apply()

    var eveningMinute: Int
        get() = prefs.getInt(KEY_EVENING_MINUTE, 0)
        set(value) = prefs.edit().putInt(KEY_EVENING_MINUTE, value).apply()

    var isSpecialDayEnabled: Boolean
        get() = prefs.getBoolean(KEY_SPECIAL_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_SPECIAL_ENABLED, value).apply()

    var isInitialSetupDone: Boolean
        get() = prefs.getBoolean(KEY_INITIAL_SETUP_DONE, false)
        set(value) = prefs.edit().putBoolean(KEY_INITIAL_SETUP_DONE, value).apply()
}
