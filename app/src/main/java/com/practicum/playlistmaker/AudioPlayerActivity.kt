package com.practicum.playlistmaker

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var viewHolder: AudioPlayerViewHolder


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

        val playAndPauseButton by lazy { findViewById<ImageButton>(R.id.play_and_pause_track_player_id) }
        val likeButton by lazy { findViewById<ImageButton>(R.id.like_track_in_player_id) }
        val addPlaylistButton by lazy { findViewById<ImageButton>(R.id.add_in_playlist_player_id) }
        val arrowBack by lazy { findViewById<MaterialToolbar>(R.id.arrow_back_player_id) }
        var isActivePlayAndPause = false
        var isActiveLikeButton = false
        var isActiveAddPlaylistButton = false


        viewHolder = AudioPlayerViewHolder(findViewById(R.id.main_audio_player))
        viewHolder.bind(SavedData.savedTrackOpen)

        arrowBack.setNavigationOnClickListener {
            finish()
        }
        playAndPauseButton.setOnClickListener {
            isActivePlayAndPause = !isActivePlayAndPause
            playAndPauseButton.isSelected = isActivePlayAndPause
        }

        likeButton.setOnClickListener {
            isActiveLikeButton = !isActiveLikeButton
            likeButton.isSelected = isActiveLikeButton
        }

        addPlaylistButton.setOnClickListener {
            isActiveAddPlaylistButton = !isActiveAddPlaylistButton
            addPlaylistButton.isSelected = isActiveAddPlaylistButton
        }
    }
}