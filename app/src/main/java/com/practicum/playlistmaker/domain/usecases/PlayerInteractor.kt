package com.practicum.playlistmaker.domain.usecases

import com.practicum.playlistmaker.domain.repository.PlayerRepository

class PlayerInteractor(private val repository: PlayerRepository) {
    fun prepare(url: String?) = repository.preparePlayer(url)
    fun play() = repository.startPlayer()
    fun pause() = repository.pausePlayer()
    fun release() = repository.releasePlayer()
    fun getPosition() = repository.getCurrentPosition()
    fun isPlaying() = repository.isPlaying()
}