package com.example.dacs3

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class Adapter_Song_List(private val list: List<Outdata_Song_List>) :
    RecyclerView.Adapter<Adapter_Song_List.SongViewHolder>() {

    class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgSong: ImageView = itemView.findViewById(R.id.imgSong)
        val txtSongName: TextView = itemView.findViewById(R.id.txtSongName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.custome_song_list, parent, false)
        return SongViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = list[position]
        Glide.with(holder.itemView.context)
            .load(song.image)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.error)
            .into(holder.imgSong)

        holder.txtSongName.text = song.song_name
    }

    override fun getItemCount(): Int = list.size
}
