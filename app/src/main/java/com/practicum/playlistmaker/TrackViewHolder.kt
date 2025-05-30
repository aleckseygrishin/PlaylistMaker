package com.practicum.playlistmaker

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TrackViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    private val bandImageId : ImageView = itemView.findViewById(R.id.band_image_id)
    private val trackNameId : TextView = itemView.findViewById(R.id.track_name_id)
    private val bandNameId : TextView = itemView.findViewById(R.id.band_name_id)
    private val timeSongId : TextView = itemView.findViewById(R.id.time_song_id)
    private val roundImage : Int = dpToPx(2.0f, itemView.context)

    fun bind(item: Track) {
        val url = item.artworkUrl100
        Glide.with(itemView)
            .load(url)
            .placeholder(R.drawable.ic_no_eathernet_album_image)
            .centerInside()
            .transform(RoundedCorners(roundImage))
            .into(bandImageId)
        trackNameId.text = item.trackName
        bandNameId.text = item.artistName
        timeSongId.text = item.trackTime

    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }

}
