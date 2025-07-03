package com.practicum.playlistmaker

import android.os.Build
import android.os.Bundle
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
    private val likeButton by lazy { findViewById<ImageButton>(R.id.like_track_in_player_id) }
    private val addPlaylistButton by lazy { findViewById<ImageButton>(R.id.add_in_playlist_player_id) }
    private val arrowBack by lazy { findViewById<MaterialToolbar>(R.id.arrow_back_player_id) }
    private val logoTrackPlayer by lazy { findViewById<ImageView>(R.id.logo_track_player_id) }
    private val nameTrackPlayer by lazy { findViewById<TextView>(R.id.track_name_player_id) }
    private val bandNamePlayer by lazy { findViewById<TextView>(R.id.band_name_player_id) }
    private val timeTrackPlayer by lazy { findViewById<TextView>(R.id.track_time_player_id) }
    private val albumNamePlayer by lazy { findViewById<TextView>(R.id.album_track_player_id) }
    private val yearTrackPlayer by lazy { findViewById<TextView>(R.id.year_track_player_id) }
    private val styleTrackPlayer by lazy { findViewById<TextView>(R.id.style_track_player_id) }
    private val countryTrackPlayer by lazy { findViewById<TextView>(R.id.country_track_player_id) }

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

        Log.d("INTENT_DEBUG", "Intent extras: ${intent.extras?.keySet()}")

        val track: Track? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(AllKeys.KEY_TRACK_SWITCH_ACTIVITY, Track::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<Track>(AllKeys.KEY_TRACK_SWITCH_ACTIVITY)
        }
        track?.let {
            pullDataPlayer(it)
        }

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
}