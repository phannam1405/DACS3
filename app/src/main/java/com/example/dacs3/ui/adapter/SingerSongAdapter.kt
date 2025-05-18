package com.example.dacs3.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dacs3.R
import com.example.dacs3.data.model.DataSongList
import com.example.dacs3.databinding.CustomSingerSongBinding

class SingerSongAdapter(
    private val songs: List<DataSongList>,
    private val onItemClick: (DataSongList) -> Unit
) : RecyclerView.Adapter<SingerSongAdapter.SingerSongViewHolder>() {

    inner class SingerSongViewHolder(val binding: CustomSingerSongBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingerSongViewHolder {
        val binding = CustomSingerSongBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SingerSongViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SingerSongViewHolder, position: Int) {
        val song = songs[position]

        // Set data to views
        holder.binding.txtTitle.text = song.songName ?: "Unknown"
        holder.binding.txtSinger.text = song.singerName ?: "Unknown"
        holder.binding.txtTime.text =  song.category

        // Load image using Glide
        Glide.with(holder.itemView.context)
            .load(song.image)
            .placeholder(R.drawable.error)
            .into(holder.binding.imgMusic)

        // Handle item click
        holder.itemView.setOnClickListener {
            onItemClick(song)
        }
    }

    override fun getItemCount(): Int = songs.size

    fun getItem(position: Int): DataSongList {
        return songs[position]
    }
}