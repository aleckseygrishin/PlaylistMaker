package com.practicum.playlistmaker

import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.appbar.MaterialToolbar
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {

    private val playAndPauseButton by lazy { findViewById<ImageButton>(R.id.play_and_pause_track_player_id) }
    private val logoTrackPlayer by lazy { findViewById<ImageView>(R.id.logo_track_player_id) }
    private val nameTrackPlayer by lazy { findViewById<TextView>(R.id.track_name_player_id) }
    private val bandNamePlayer by lazy { findViewById<TextView>(R.id.band_name_player_id) }
    private val timeTrackPlayer by lazy { findViewById<TextView>(R.id.track_time_player_id) }
    private val albumNamePlayer by lazy { findViewById<TextView>(R.id.album_track_player_id) }
    private val yearTrackPlayer by lazy { findViewById<TextView>(R.id.year_track_player_id) }
    private val styleTrackPlayer by lazy { findViewById<TextView>(R.id.style_track_player_id) }
    private val countryTrackPlayer by lazy { findViewById<TextView>(R.id.country_track_player_id) }
    private val mainTimerTrack by lazy { findViewById<TextView>(R.id.timer_track_player_id) }
    private var handlerMain: Handler? = null
    private var playerState = STATE_DEFAULT

    private val mediaPlayer = MediaPlayer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

        handlerMain = Handler(Looper.getMainLooper())

        val playAndPauseButton by lazy { findViewById<ImageButton>(R.id.play_and_pause_track_player_id) }
        val likeButton by lazy { findViewById<ImageButton>(R.id.like_track_in_player_id) }
        val addPlaylistButton by lazy { findViewById<ImageButton>(R.id.add_in_playlist_player_id) }
        val arrowBack by lazy { findViewById<MaterialToolbar>(R.id.arrow_back_player_id) }
        var isActiveLikeButton = false
        var isActiveAddPlaylistButton = false
        val track = getSavedTrack()

        Log.d("INTENT_DEBUG", "Intent extras: ${intent.extras?.keySet()}")


        track?.let {
            pullDataPlayer(it)
        }

        arrowBack.setNavigationOnClickListener {
            finish()
        }

        preparePlayer()

        playAndPauseButton.setOnClickListener {
            playbackControl()
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

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        stopPositionUpdates()
    }

    fun pullDataPlayer(track: Track) {
        val roundImage = dpToPx(8.0f)

        Glide.with(logoTrackPlayer.context)
            .load(getCoverArtwork(track.artworkUrl100))
            .placeholder(R.drawable.ic_placeholder_312)
            .centerInside()
            .transform(RoundedCorners(roundImage))
            .into(logoTrackPlayer)

        nameTrackPlayer.text = track.trackName
        bandNamePlayer.text = track.artistName
        timeTrackPlayer.text = msTimeToMinutes(track.trackTime.toLong())
        albumNamePlayer.text = track.collectionName ?: getString(R.string.not_available)
        yearTrackPlayer.text = track.releaseDate?.let { getYear(it) } ?: getString(R.string.not_available)
        styleTrackPlayer.text = track.primaryGenreName ?: getString(R.string.not_available)
        countryTrackPlayer.text = track.country ?: getString(R.string.not_available)
    }

    private fun dpToPx(dp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            resources.displayMetrics).toInt()
    }

    fun getCoverArtwork(url: String) : String = url.replaceAfterLast('/',"512x512bb.jpg")
    fun getYear(fullDate: String) : String = fullDate.take(4)

    private fun msTimeToMinutes(time: Long): String? {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(time)
    }

    private fun preparePlayer() {
        val track = getSavedTrack()
        mediaPlayer.setDataSource(track!!.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playAndPauseButton.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playAndPauseButton.isSelected = false
            playerState = STATE_PREPARED
        }
    }

    private fun playbackControl() {
        when(playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playAndPauseButton.isSelected = true
        playerState = STATE_PLAYING
        startPositionUpdates()
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playAndPauseButton.isSelected = false
        playerState = STATE_PAUSED
        stopPositionUpdates()
    }

    private fun timeRunTrack() : Runnable {
        return object : Runnable {
            override fun run() {
                if(playerState == STATE_PLAYING) {
                    val currentPosition = mediaPlayer.currentPosition
                    mainTimerTrack.setText(
                        msTimeToMinutes(currentPosition.toLong())
                    )
                    handlerMain?.postDelayed(this, DELAY_TRACK_REFRESH_TIME)
                }
            }
        }
    }

    private fun startPositionUpdates() {
            handlerMain?.removeCallbacks(timeRunTrack())
            handlerMain?.post(timeRunTrack())
    }

    private fun stopPositionUpdates() {
        handlerMain?.removeCallbacks(timeRunTrack())
    }


    private fun getSavedTrack() : Track? {
        val track: Track? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(AllKeys.KEY_TRACK_SWITCH_ACTIVITY, Track::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<Track>(AllKeys.KEY_TRACK_SWITCH_ACTIVITY)
        }

        return track
    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3

        private const val DELAY_TRACK_REFRESH_TIME = 300L
    }
}