package com.practicum.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TrackAdapter(private val onTrackClick: (Track) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var tracks = ArrayList<Track>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        if (viewType == TYPE_TRACK)
            return TrackViewHolder(inflater.inflate(R.layout.track_item, parent, false))
        else if (viewType == TYPE_TRACK_NOT_FOUND)
            return TrackNotFoundViewHolder(inflater.inflate(R.layout.track_not_found, parent, false))

        return TrackNoEthernetViewHolder(inflater.inflate(R.layout.track_no_ethernet, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val track = tracks[position]
        when (holder) {
            is TrackViewHolder -> holder.bind(track)
            is TrackNotFoundViewHolder -> holder.bind()
            is TrackNoEthernetViewHolder -> holder.bind()
        }

        holder.itemView.setOnClickListener {
            onTrackClick(track)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(tracks[position].typeRes) {
            Track.TRACK_TYPE_RES_NO_ETHERNET -> TYPE_TRACK_NO_ETHERNET
            Track.TRACK_TYPE_RES_NOT_FOUND -> TYPE_TRACK_NOT_FOUND
            else -> TYPE_TRACK
        }
    }

    override fun getItemCount(): Int = tracks.size

    companion object {
        private const val TYPE_TRACK = 0
        private const val TYPE_TRACK_NOT_FOUND = 1
        private const val TYPE_TRACK_NO_ETHERNET = 2
    }
}
