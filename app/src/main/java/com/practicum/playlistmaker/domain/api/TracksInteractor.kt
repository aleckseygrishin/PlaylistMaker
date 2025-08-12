package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.Track

interface TracksInteractor {
    fun searchTracks(
        expression: String,
        consumer: TracksConsumer,
        debounceDelay: Long = DEFAULT_DEBOUNCE_DELAY
    )

    interface TracksConsumer {

        fun consume(foundTracks: List<Track>)

        fun onError(error: Exception)

    }

    companion object {
        const val DEFAULT_DEBOUNCE_DELAY = 2000L
    }
}