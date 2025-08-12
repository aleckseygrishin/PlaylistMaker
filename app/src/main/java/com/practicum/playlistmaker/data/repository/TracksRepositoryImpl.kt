package com.practicum.playlistmaker.data.repository

import com.practicum.playlistmaker.data.NetworkClient
import com.practicum.playlistmaker.data.dto.TracksSearchRequest
import com.practicum.playlistmaker.data.dto.TracksSearchResponse
import com.practicum.playlistmaker.data.mapper.TrackMapper
import com.practicum.playlistmaker.domain.api.TracksRepository
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.models.TrackTypeRes

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val trackMapper: TrackMapper
) : TracksRepository {

    override fun searchTracks(expression: String): List<Track> {
        try {
            val response = networkClient.doRequest(TracksSearchRequest(expression))

            return when {
                response !is TracksSearchResponse -> {
                    listOf(createErrorTrack(TrackTypeRes.NO_INTERNET))
                }

                response.resultCode != 200 -> {
                    listOf(createErrorTrack(TrackTypeRes.NO_INTERNET))
                }

                response.results.isEmpty() -> {
                    listOf(createErrorTrack(TrackTypeRes.NOT_FOUND))
                }

                else -> response.results.mapNotNull { trackDto ->
                    trackMapper.mapToDomain(trackDto)
                }
            }
        } catch (e: Exception) {
            listOf(createErrorTrack(TrackTypeRes.NO_INTERNET))
        }
        return listOf(createErrorTrack(TrackTypeRes.NO_INTERNET))

    }

    private fun createErrorTrack(typeRes: TrackTypeRes): Track {
        return Track(
            trackName = "",
            artistName = "",
            trackTimeMillis = "",
            artworkUrl100 = "",
            trackId = 0,
            collectionName = "",
            releaseDate = "",
            primaryGenreName = "",
            country = "",
            previewUrl = "",
            typeRes = typeRes
        )
    }
}