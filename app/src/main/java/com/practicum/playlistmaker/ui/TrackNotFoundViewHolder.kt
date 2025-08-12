package com.practicum.playlistmaker.ui

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R


class TrackNotFoundViewHolder(itemView: View) :
    RecyclerView.ViewHolder(itemView) {

    private val message : TextView = itemView.findViewById(R.id.message_not_found)

    fun bind() {
        message.setText(R.string.track_not_found_text)
    }

}
