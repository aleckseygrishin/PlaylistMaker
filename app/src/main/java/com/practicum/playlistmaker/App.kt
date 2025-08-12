package com.practicum.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.domain.usecases.SettingsInteractor

class App : Application() {

    lateinit var settingsInteractor: SettingsInteractor

    override fun onCreate() {
        super.onCreate()

        settingsInteractor = Creator.provideAppInteractor(this)

        if (settingsInteractor.isDarkThemeEnabled()) {
            switchTheme(true)
        }
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        if (darkThemeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}
