package com.practicum.playlistmaker.domain.repository

interface SettingsRepository {
    fun getDarkThemeEnabled(): Boolean
    fun setDarkThemeEnabled(enabled: Boolean)
}