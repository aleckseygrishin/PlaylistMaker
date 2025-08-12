package com.practicum.playlistmaker.domain.usecases

import com.practicum.playlistmaker.domain.repository.SettingsRepository

class SettingsInteractor(private val repository: SettingsRepository) {
    fun isDarkThemeEnabled() = repository.getDarkThemeEnabled()
    fun setDarkThemeEnabled(enabled: Boolean) = repository.setDarkThemeEnabled(enabled)
}