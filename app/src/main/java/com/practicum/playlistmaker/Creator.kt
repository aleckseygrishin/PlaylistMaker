package com.practicum.playlistmaker

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.practicum.playlistmaker.data.mapper.TrackMapper
import com.practicum.playlistmaker.data.repository.TracksRepositoryImpl
import com.practicum.playlistmaker.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.data.repository.PlayerRepositoryImpl
import com.practicum.playlistmaker.data.repository.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.data.repository.SettingsRepositoryImpl
import com.practicum.playlistmaker.domain.api.TracksInteractor
import com.practicum.playlistmaker.domain.api.TracksRepository
import com.practicum.playlistmaker.domain.impl.TracksInteractorImpl
import com.practicum.playlistmaker.domain.repository.SearchHistoryRepository
import com.practicum.playlistmaker.domain.repository.SettingsRepository
import com.practicum.playlistmaker.domain.usecases.PlayerInteractor
import com.practicum.playlistmaker.domain.usecases.SettingsInteractor

object Creator {
    private fun getTrackMapper(): TrackMapper {
        return TrackMapper()
    }
    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient(), getTrackMapper())
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }

    private fun provideSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences("search_prefs", Context.MODE_PRIVATE)
    }

    private fun provideGson(): Gson {
        return Gson()
    }

    fun provideSearchHistoryRepository(context: Context): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(
            sharedPreferences = provideSharedPreferences(context),
            gson = provideGson()
        )
    }

    fun providePlayerInteractor() : PlayerInteractor {
        return PlayerInteractor(getPlayerRepositoryImpl())
    }

    private fun getPlayerRepositoryImpl() : PlayerRepositoryImpl {
        return PlayerRepositoryImpl()
    }

    fun provideSettingsRepository(context: Context): SettingsRepository {
        val sharedPref = context.getSharedPreferences(
            SettingsRepositoryImpl.KEY_SWITCH_THEME,
            Context.MODE_PRIVATE
        )
        return SettingsRepositoryImpl(sharedPref)
    }

    fun provideSettingsInteractor(context: Context): SettingsInteractor {
        return SettingsInteractor(provideSettingsRepository(context))
    }

    fun provideAppInteractor(context: Context): SettingsInteractor {
        return provideSettingsInteractor(context)
    }
}