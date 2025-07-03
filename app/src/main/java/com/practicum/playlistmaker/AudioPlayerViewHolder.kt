package com.practicum.playlistmaker

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerViewHolder(itemView: View){

    private val logoTrackPlayer by lazy { itemView.findViewById<ImageView>(R.id.logo_track_player_id) }
    private val nameTrackPlayer by lazy { itemView.findViewById<TextView>(R.id.track_name_player_id) }
    private val bandNamePlayer by lazy { itemView.findViewById<TextView>(R.id.band_name_player_id) }
    private val timeTrackPlayer by lazy { itemView.findViewById<TextView>(R.id.track_time_player_id) }
    private val albumNamePlayer by lazy { itemView.findViewById<TextView>(R.id.album_track_player_id) }
    private val yearTrackPlayer by lazy { itemView.findViewById<TextView>(R.id.year_track_player_id) }
    private val styleTrackPlayer by lazy { itemView.findViewById<TextView>(R.id.style_track_player_id) }
    private val countryTrackPlayer by lazy { itemView.findViewById<TextView>(R.id.country_track_player_id) }
    private val roundImage : Int = dpToPx(8.0f, itemView.context)

    fun bind(track: Track) {
        Glide.with(logoTrackPlayer.context)
            .load(getCoverArtwork(track.artworkUrl100))
            .placeholder(R.drawable.ic_placeholder_312)
            .centerInside()
            .transform(RoundedCorners(roundImage))
            .into(logoTrackPlayer)
        nameTrackPlayer.text = track.trackName
        bandNamePlayer.setText("")
        bandNamePlayer.text = track.artistName
        timeTrackPlayer.text = msTimeToMinutes(track.trackTime.toLong())
        albumNamePlayer.text = track.collectionName
        yearTrackPlayer.text = getYear(track.releaseDate)
        styleTrackPlayer.text = track.primaryGenreName
        countryTrackPlayer.text = track.country
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }

    fun getCoverArtwork(url: String) : String = url.replaceAfterLast('/',"512x512bb.jpg")
    fun getYear(fullDate: String) : String = fullDate.take(4)

    private fun msTimeToMinutes(time: Long): String? {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(time)
    }

}
