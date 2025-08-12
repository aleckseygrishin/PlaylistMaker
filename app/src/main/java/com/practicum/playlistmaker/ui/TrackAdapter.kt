package com.practicum.playlistmaker.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.models.TrackTypeRes

class TrackAdapter(private val onTrackClick: (Track) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var tracks = ArrayList<Track>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TrackTypeRes.NOT_FOUND.ordinal -> TrackNotFoundViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.track_not_found, parent, false)
            )
            TrackTypeRes.NO_INTERNET.ordinal -> TrackNoEthernetViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.track_no_ethernet, parent, false)
            )
            else -> TrackViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val track = tracks[position]

        when (holder) {
            is TrackViewHolder -> holder.bind(track)
            is TrackNotFoundViewHolder -> holder.bind()
            is TrackNoEthernetViewHolder -> holder.bind()
        }

        if (track.typeRes == TrackTypeRes.DEFAULT) {
            holder.itemView.setOnClickListener { onTrackClick(track) }
        } else {
            holder.itemView.setOnClickListener(null)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return tracks[position].typeRes.ordinal
    }

    override fun getItemCount(): Int = tracks.size
}