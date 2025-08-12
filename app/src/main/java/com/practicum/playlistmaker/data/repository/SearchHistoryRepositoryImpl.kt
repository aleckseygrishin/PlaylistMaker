package com.practicum.playlistmaker.data.repository

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.repository.SearchHistoryRepository
import androidx.core.content.edit

class SearchHistoryRepositoryImpl(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) : SearchHistoryRepository {

    override fun addTrack(track: Track) {
        val currentHistory = getHistory().toMutableList().apply {
            removeAll { it.trackId == track.trackId }
            if (size >= MAX_HISTORY_SIZE) removeAt(lastIndex)
            add(0, track)
        }
        saveHistory(currentHistory)
    }

    override fun clearHistory() = sharedPreferences.edit { remove(HISTORY_KEY) }

    override fun getHistory(): List<Track> {
        return sharedPreferences.getString(HISTORY_KEY, null)?.let { json ->
            try {
                gson.fromJson(json, object : TypeToken<List<Track>>() {}.type) ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }
        } ?: emptyList()
    }

    override fun isHistoryEmpty() = getHistory().isEmpty()

    private fun saveHistory(tracks: List<Track>) {
        sharedPreferences.edit { putString(HISTORY_KEY, gson.toJson(tracks)) }
    }

    companion object {
        private const val HISTORY_KEY = "key_add_history_track"
        private const val MAX_HISTORY_SIZE = 10
    }
}