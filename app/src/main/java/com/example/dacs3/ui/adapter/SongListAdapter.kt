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

    override fun getItemCount(): Int = list.size

    fun setOnItemClickListenner(clickListenner: onItemClickListenner) {
        mListener = clickListenner
    }

    class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val btnDownload: ImageView = itemView.findViewById(R.id.btnDownload)
        val imgSong: ImageView = itemView.findViewById(R.id.imgSong)
        val txtSongName: TextView = itemView.findViewById(R.id.txtSongName)
    }

    // Tạo view holder cho mỗi item trong danh sách
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.custom_song_list, parent, false)
        return SongViewHolder(itemView)
    }

    // Gắn dữ liệu cho từng item trong danh sách
    override fun onBindViewHolder(holder: SongViewHolder, @SuppressLint("RecyclerView") position: Int) {
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
