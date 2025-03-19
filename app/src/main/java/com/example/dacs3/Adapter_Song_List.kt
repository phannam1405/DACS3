package com.example.dacs3

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class Adapter_Song_List(private val list: List<Outdata_Song_List>) : RecyclerView.Adapter<Adapter_Song_List.SongViewHolder>() {

    private lateinit var mListener: onItemClickListenner
    private var selectedPosition: Int = -1 // Lưu vị trí item có nút download

    interface onItemClickListenner {
        fun onItemClick(position: Int)
        fun onDownloadClicked(song: Outdata_Song_List)
    }

    fun setOnItemClickListenner(clickListenner: onItemClickListenner) {
        mListener = clickListenner
    }

    // Hàm này sẽ gọi khi bấm vào màn hình trống để ẩn nút download
    fun clearSelectedPosition() {
        selectedPosition = -1

        notifyDataSetChanged()
    }

    class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val btnDownload: ImageView = itemView.findViewById(R.id.btnDownload)
        val imgSong: ImageView = itemView.findViewById(R.id.imgSong)
        val txtSongName: TextView = itemView.findViewById(R.id.txtSongName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.custome_song_list, parent, false)
        return SongViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SongViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val song = list[position]

        Glide.with(holder.itemView.context)
            .load(song.image)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.error)
            .into(holder.imgSong)

        holder.txtSongName.text = song.song_name

        // Hiển thị nút download nếu item đang được chọn
        holder.btnDownload.visibility = if (selectedPosition == position) View.VISIBLE else View.GONE

        // Xử lý khi giữ lâu vào item
        holder.itemView.setOnLongClickListener {
            selectedPosition = position
            notifyDataSetChanged()
            true
        }

        // Xử lý khi bấm vào nút download
        holder.btnDownload.setOnClickListener {
            mListener.onDownloadClicked(song)
        }

        // Xử lý khi click vào item
        holder.itemView.setOnClickListener {
            mListener.onItemClick(position)
        }
    }

    override fun getItemCount(): Int = list.size
}
