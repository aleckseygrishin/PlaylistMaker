package com.practicum.playlistmaker

import com.google.gson.annotations.SerializedName

data class Track(
    val trackName: String,
    val artistName: String,
    @SerializedName("trackTimeMillis") val trackTime: String,
    val artworkUrl100: String,
    var typeRes: String
) {

    companion object {
        const val TRACK_TYPE_RES_NOT_FOUND = "NOT_FOUND"
        const val TRACK_TYPE_RES_NO_ETHERNET = "NO_ETHERNET"
    }
}
