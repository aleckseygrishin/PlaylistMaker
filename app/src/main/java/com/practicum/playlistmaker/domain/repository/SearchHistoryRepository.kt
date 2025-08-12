package com.practicum.playlistmaker.domain.repository

import com.practicum.playlistmaker.domain.models.Track

interface SearchHistoryRepository {
    fun addTrack(track: Track)
    fun clearHistory()
    fun getHistory(): List<Track>
    fun isHistoryEmpty(): Boolean
}