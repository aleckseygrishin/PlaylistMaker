package com.practicum.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory(private val shared: SharedPreferences) {

    fun addTrackHistory(track: Track) {
        val json = readData()
        val historyTracks = createTracksHistoryFromJson(json)
        for(history in historyTracks) {
            if(history.trackId == track.trackId) {
                historyTracks.remove(track)
                addHistoryAndSave(historyTracks, track)
                return
            }
        }
        if(historyTracks.size == SIZE_HISTORY_ARR) {
            historyTracks.removeAt(historyTracks.lastIndex)
            addHistoryAndSave(historyTracks, track)
            return
        } else {
            addHistoryAndSave(historyTracks, track)
            return
        }
    }

    private fun addHistoryAndSave(history: ArrayList<Track>, track: Track) {
        history.add(0, track)
        saveData(history)
    }

    fun readData() : String {
        return shared.getString(KEY_ADD_HISTORY_TRACK, null).toString()
    }

    fun removeData() {
        shared.edit()
            .remove(KEY_ADD_HISTORY_TRACK)
            .apply()
    }

    fun saveData(historyTracks: ArrayList<Track>) {
        shared.edit()
            .putString(KEY_ADD_HISTORY_TRACK, createJsonFromTracksHistory(historyTracks))
            .apply()
    }

    fun getEmptyOrNullSharedTracks(): Boolean {
        return getArrHistoryTrack().isNullOrEmpty()
    }

    fun getArrHistoryTrack(): ArrayList<Track> {
        return createTracksHistoryFromJson(readData())
    }

    private fun createJsonFromTracksHistory(tracks: ArrayList<Track>): String {
        return Gson().toJson(tracks)
    }

    fun createTracksHistoryFromJson(json: String): ArrayList<Track> {
        return try {
            val gson = Gson()
            val type = object : TypeToken<ArrayList<Track>>() {}.type
            gson.fromJson(json, type) ?: arrayListOf()
        } catch (e: Exception) {
            e.printStackTrace()
            arrayListOf()
        }
    }

    companion object {
        private const val SIZE_HISTORY_ARR = 10
        const val KEY_ADD_HISTORY_TRACK = "key_add_history_track"
    }
}