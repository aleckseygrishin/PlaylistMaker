package com.practicum.playlistmaker

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Track(
    val trackName: String,
    val artistName: String,
    @SerializedName("trackTimeMillis") val trackTime: String,
    val artworkUrl100: String,
    val trackId: Int,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    var typeRes: String? = null
) : Parcelable {

    companion object {
        const val TRACK_TYPE_RES_NOT_FOUND = "NOT_FOUND"
        const val TRACK_TYPE_RES_NO_ETHERNET = "NO_ETHERNET"
    }
}
