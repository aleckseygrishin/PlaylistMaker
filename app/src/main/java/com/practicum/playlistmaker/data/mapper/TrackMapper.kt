package com.practicum.playlistmaker.data.mapper

import android.util.Log
import com.practicum.playlistmaker.data.dto.TrackDto
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.models.TrackTypeRes
import java.util.Locale

class TrackMapper {
    fun mapToDomain(dto: TrackDto): Track? {
        return try {
            Track(
                trackName = dto.trackName ?: "",
                artistName = dto.artistName ?: "",
                trackTimeMillis = msToTimeFormat(dto.trackTimeMillis) ?: "",
                artworkUrl100 = dto.artworkUrl100 ?: "",
                trackId = dto.trackId ?: 0,
                collectionName = dto.collectionName ?: "",
                releaseDate = dto.releaseDate ?: "",
                primaryGenreName = dto.primaryGenreName ?: "",
                country = dto.country ?: "",
                previewUrl = dto.previewUrl ?: "",
                typeRes = TrackTypeRes.DEFAULT
            )
        } catch (e: Exception) {
            Log.e("MAPPER", "Failed to map track: ${e.message}", e)
            null
        }
    }

    private fun msToTimeFormat(ms: Long?): String {
        if (ms == null) return "0:00"
        val seconds = ms / 1000
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format(Locale.getDefault(), "%d:%02d", minutes, remainingSeconds)
    }
}