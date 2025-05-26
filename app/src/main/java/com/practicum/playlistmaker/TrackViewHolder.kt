package com.practicum.playlistmaker

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

    fun bind(item: Track) {
        val url = item.artworkUrl100
        Glide.with(itemView)
            .load(url)
            .placeholder(R.drawable.ic_no_eathernet_album_image)
            .centerInside()
            .transform(RoundedCorners(2))
            .into(bandImageId)
        trackNameId.text = item.trackName
        bandNameId.text = item.artistName
        timeSongId.text = item.trackTime

    }

}