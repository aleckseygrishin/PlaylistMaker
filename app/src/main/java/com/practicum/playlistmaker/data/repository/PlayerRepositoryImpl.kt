package com.practicum.playlistmaker.data.repository

import android.media.MediaPlayer
import com.practicum.playlistmaker.domain.repository.PlayerRepository

class PlayerRepositoryImpl : PlayerRepository {
    private var mediaPlayer: MediaPlayer? = null
    private var prepared = false

    override fun preparePlayer(url: String?) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(url)
            prepareAsync()
            setOnPreparedListener {
                prepared = true
            }
            setOnCompletionListener {
                prepared = true
            }
        }
    }

    override fun startPlayer() {
        if (prepared) {
            mediaPlayer?.start()
        }
    }

    override fun pausePlayer() {
        mediaPlayer?.pause()
    }

    override fun releasePlayer() {
        mediaPlayer?.release()
        mediaPlayer = null
        prepared = false
    }

    override fun getCurrentPosition(): Int = mediaPlayer?.currentPosition ?: 0

    override fun isPlaying(): Boolean = mediaPlayer?.isPlaying ?: false
}