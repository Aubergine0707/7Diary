package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import java.security.MessageDigest

class SecurityPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "offline_diary_security_prefs",
        Context.MODE_PRIVATE
    )

    companion object {
        private const val KEY_LOCK_ENABLED = "key_lock_enabled"
        private const val KEY_PIN_HASH = "key_pin_hash"
        private const val KEY_BIOMETRIC_ENABLED = "key_biometric_enabled"
        private const val KEY_THEME_MODE = "key_theme_mode" // "SYSTEM", "LIGHT", "DARK"
        private const val KEY_COLOR_PALETTE = "key_color_palette"
        private const val PREFIX_CYCLE_REFLECTION = "key_cycle_refl_"
        private const val SALT = "OFFLINE_DIARY_SECRET_SALT_2026"
    }

    fun getCycleReflection(cycleKey: String): String {
        return prefs.getString(PREFIX_CYCLE_REFLECTION + cycleKey, "") ?: ""
    }

    fun setCycleReflection(cycleKey: String, reflection: String) {
        prefs.edit().putString(PREFIX_CYCLE_REFLECTION + cycleKey, reflection).apply()
    }

    var isLockEnabled: Boolean
        get() = prefs.getBoolean(KEY_LOCK_ENABLED, false)
        set(value) = prefs.edit().putBoolean(KEY_LOCK_ENABLED, value).apply()

    var isBiometricEnabled: Boolean
        get() = prefs.getBoolean(KEY_BIOMETRIC_ENABLED, false)
        set(value) = prefs.edit().putBoolean(KEY_BIOMETRIC_ENABLED, value).apply()

    var themeMode: String
        get() = prefs.getString(KEY_THEME_MODE, "SYSTEM") ?: "SYSTEM"
        set(value) = prefs.edit().putString(KEY_THEME_MODE, value).apply()

    var colorPalette: String
        get() = prefs.getString(KEY_COLOR_PALETTE, "NOTHING") ?: "NOTHING"
        set(value) = prefs.edit().putString(KEY_COLOR_PALETTE, value).apply()

    fun isPinSet(): Boolean {
        val hash = prefs.getString(KEY_PIN_HASH, null)
        return !hash.isNullOrEmpty()
    }

    fun setPin(pin: String) {
        val hash = hashPin(pin)
        prefs.edit()
            .putString(KEY_PIN_HASH, hash)
            .putBoolean(KEY_LOCK_ENABLED, true)
            .apply()
    }

    fun verifyPin(pin: String): Boolean {
        val storedHash = prefs.getString(KEY_PIN_HASH, null) ?: return false
        val enteredHash = hashPin(pin)
        return storedHash == enteredHash
    }

    fun clearSecurityLock() {
        prefs.edit()
            .remove(KEY_PIN_HASH)
            .putBoolean(KEY_LOCK_ENABLED, false)
            .putBoolean(KEY_BIOMETRIC_ENABLED, false)
            .apply()
    }

    private fun hashPin(pin: String): String {
        val salted = "$SALT:$pin"
        val bytes = MessageDigest.getInstance("SHA-256").digest(salted.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
