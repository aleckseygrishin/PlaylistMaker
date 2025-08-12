package com.practicum.playlistmaker.ui

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.appbar.MaterialToolbar
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.usecases.PlayerInteractor

class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var interactor: PlayerInteractor
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var updatePositionRunnable: Runnable

    private val playAndPauseButton by lazy(LazyThreadSafetyMode.NONE) { findViewById<ImageButton>(R.id.play_and_pause_track_player_id) }
    private val logoTrackPlayer by lazy(LazyThreadSafetyMode.NONE) { findViewById<ImageView>(R.id.logo_track_player_id) }
    private val nameTrackPlayer by lazy(LazyThreadSafetyMode.NONE) { findViewById<TextView>(R.id.track_name_player_id) }
    private val bandNamePlayer by lazy(LazyThreadSafetyMode.NONE) { findViewById<TextView>(R.id.band_name_player_id) }
    private val timeTrackPlayer by lazy(LazyThreadSafetyMode.NONE) { findViewById<TextView>(R.id.track_time_player_id) }
    private val albumNamePlayer by lazy(LazyThreadSafetyMode.NONE) { findViewById<TextView>(R.id.album_track_player_id) }
    private val yearTrackPlayer by lazy(LazyThreadSafetyMode.NONE) { findViewById<TextView>(R.id.year_track_player_id) }
    private val styleTrackPlayer by lazy(LazyThreadSafetyMode.NONE) { findViewById<TextView>(R.id.style_track_player_id) }
    private val countryTrackPlayer by lazy(LazyThreadSafetyMode.NONE) { findViewById<TextView>(R.id.country_track_player_id) }
    private val mainTimerTrack by lazy(LazyThreadSafetyMode.NONE) { findViewById<TextView>(R.id.timer_track_player_id) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

        interactor = Creator.providePlayerInteractor()

        val track = getSavedTrack()
        track?.let { updateUI(it) }

        setupButtons()
        setupPositionUpdates()
    }

    private fun setupButtons() {
        findViewById<MaterialToolbar>(R.id.arrow_back_player_id).setNavigationOnClickListener {
            finish()
        }

        playAndPauseButton.setOnClickListener {
            if (interactor.isPlaying()) {
                interactor.pause()
                playAndPauseButton.isSelected = false
                stopPositionUpdates()
            } else {
                interactor.play()
                playAndPauseButton.isSelected = true
                startPositionUpdates()
            }
        }

        findViewById<ImageButton>(R.id.like_track_in_player_id).setOnClickListener {
            it.isSelected = !it.isSelected
        }

        findViewById<ImageButton>(R.id.add_in_playlist_player_id).setOnClickListener {
            it.isSelected = !it.isSelected
        }
    }

    private fun updateUI(track: Track) {
        val roundImage = dpToPx(8.0f)

        Glide.with(logoTrackPlayer.context)
            .load(getCoverArtwork(track.artworkUrl100))
            .placeholder(R.drawable.ic_placeholder_312)
            .centerInside()
            .transform(RoundedCorners(roundImage))
            .into(logoTrackPlayer)

        nameTrackPlayer.text = track.trackName
        bandNamePlayer.text = track.artistName
        timeTrackPlayer.text = track.trackTimeMillis
        albumNamePlayer.text = track.collectionName ?: getString(R.string.not_available)
        yearTrackPlayer.text = track.releaseDate?.let { getYear(it) } ?: getString(R.string.not_available)
        styleTrackPlayer.text = track.primaryGenreName ?: getString(R.string.not_available)
        countryTrackPlayer.text = track.country ?: getString(R.string.not_available)

        interactor.prepare(track.previewUrl)
    }

    private fun setupPositionUpdates() {
        updatePositionRunnable = object : Runnable {
            override fun run() {
                if (interactor.isPlaying()) {
                    val currentPosition = interactor.getPosition()
                    mainTimerTrack.text = formatTime(currentPosition)
                    handler.postDelayed(this, DELAY_TRACK_REFRESH_TIME)
                }
            }
        }
    }

    private fun startPositionUpdates() {
        handler.post(updatePositionRunnable)
    }

    private fun stopPositionUpdates() {
        handler.removeCallbacks(updatePositionRunnable)
    }

    private fun dpToPx(dp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            resources.displayMetrics).toInt()
    }

    private fun getCoverArtwork(url: String): String = url.replaceAfterLast('/',"512x512bb.jpg")

    private fun getYear(fullDate: String): String = fullDate.take(4)

    private fun formatTime(millis: Int): String {
        val seconds = millis / 1000
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%02d:%02d", minutes, remainingSeconds)
    }

    private fun getSavedTrack(): Track? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(KEY_TRACK_SWITCH_ACTIVITY, Track::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(KEY_TRACK_SWITCH_ACTIVITY)
        }
    }

    override fun onPause() {
        super.onPause()
        interactor.pause()
        stopPositionUpdates()
    }

    override fun onDestroy() {
        super.onDestroy()
        interactor.release()
        handler.removeCallbacksAndMessages(null)
    }

    companion object {
        private const val DELAY_TRACK_REFRESH_TIME = 300L
        private const val KEY_TRACK_SWITCH_ACTIVITY = "key_track_switch_activity"
    }
}