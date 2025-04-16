package com.example.dacs3.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dacs3.data.model.DataSongList
import com.example.dacs3.R

class SongListAdapter(private val list: List<DataSongList>) : RecyclerView.Adapter<SongListAdapter.SongViewHolder>() {

    private lateinit var mListener: onItemClickListenner
    private var selectedPosition: Int = -1 // Lưu vị trí item có nút download

    interface onItemClickListenner {
        fun onItemClick(position: Int)
        fun onAddPlaylist(song: DataSongList)
    }

    // Lấy tối đa 10 bài hát từ danh sách
    override fun getItemCount(): Int = if (list.size > 10) 10 else list.size

    fun setOnItemClickListenner(clickListenner: onItemClickListenner) {
        mListener = clickListenner
    }

    class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgSong: ImageView = itemView.findViewById(R.id.imgSong)
        val txtSongName: TextView = itemView.findViewById(R.id.txtSongName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.custom_song_list, parent, false)
        return SongViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = list[position]

        // Thu viện lấy ảnh theo URL
        Glide.with(holder.itemView.context)
            .load(song.image)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.error)
            .into(holder.imgSong)
        holder.txtSongName.text = song.song_name

        // Xử lý khi click vào từng item
        holder.itemView.setOnClickListener {
            mListener.onItemClick(position)
        }
    }
}
