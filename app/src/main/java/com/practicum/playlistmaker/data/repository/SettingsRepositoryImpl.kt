package com.practicum.playlistmaker.data.repository

import android.content.SharedPreferences
import com.practicum.playlistmaker.domain.repository.SettingsRepository

class SettingsRepositoryImpl(
    private val sharedPreferences: SharedPreferences
) : SettingsRepository {

    override fun getDarkThemeEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_SWITCH_THEME, false)
    }

    override fun setDarkThemeEnabled(enabled: Boolean) {
        sharedPreferences.edit()
            .putBoolean(KEY_SWITCH_THEME, enabled)
            .apply()
    }

    companion object {
        const val KEY_SWITCH_THEME = "key_switch_theme"
    }
}