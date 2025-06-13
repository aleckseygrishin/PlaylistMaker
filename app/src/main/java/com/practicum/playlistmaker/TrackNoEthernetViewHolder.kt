package com.practicum.playlistmaker

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TrackNoEthernetViewHolder(itemView: View) :
    RecyclerView.ViewHolder(itemView) {

    private val message : TextView = itemView.findViewById(R.id.message_no_ethernet)

    fun bind() {
        message.setText(R.string.track_no_ethernet_text)
    }
}
