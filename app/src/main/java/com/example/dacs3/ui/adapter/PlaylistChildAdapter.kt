package com.example.dacs3.ui.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.dacs3.R
import com.example.dacs3.data.model.DataSongList

class PlaylistChildAdapter(
    val activity: Activity,
    var list: List<DataSongList>
) : ArrayAdapter<DataSongList>(activity, R.layout.custom_playlist_child_list, list) {

    private var itemClickListener: onItemClickListener? = null
    private var deleteClickListener: OnDeleteClickListener? = null

    private class ViewHolder(view: View) {
        val imageView: ImageView = view.findViewById(R.id.imgMusic)
        val titleView: TextView = view.findViewById(R.id.txtTitle)
        val singerView: TextView = view.findViewById(R.id.txtSinger)
        val timeView: TextView = view.findViewById(R.id.txtTime)
        val btnDelete: ImageView = view.findViewById(R.id.btnDeleteSongInPlayList)
    }

    override fun getCount(): Int = list.size

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val holder: ViewHolder
        val rowView: View

        if (convertView == null) {
            val inflater = LayoutInflater.from(context)
            rowView = inflater.inflate(R.layout.custom_playlist_child_list, parent, false)
            holder = ViewHolder(rowView)
            rowView.tag = holder
        } else {
            rowView = convertView
            holder = rowView.tag as ViewHolder
        }

        val currentItem = list[position]

        Glide.with(activity)
            .load(currentItem.image)
            .into(holder.imageView)

        holder.titleView.text = currentItem.songName
        holder.singerView.text = currentItem.singerName
        holder.timeView.text = currentItem.category

        // Click toàn item (nếu bạn vẫn muốn dùng)
        rowView.setOnClickListener {
            itemClickListener?.onItemClick(position)
        }

        // Click vào nút xoá
        holder.btnDelete.setOnClickListener {
            deleteClickListener?.onDeleteClick(position)
        }

        return rowView
    }

    fun getAllSongs(): List<DataSongList> {
        val songs = mutableListOf<DataSongList>()
        for (i in 0 until count) {
            getItem(i)?.let { songs.add(it) }
        }
        return songs
    }

    fun updateData(newList: List<DataSongList>) {
        list = newList
        Log.d("PlaylistChildAdapter", "List data updated, size: ${list.size}")
        notifyDataSetChanged()
    }

    interface onItemClickListener {
        fun onItemClick(position: Int)
    }

    interface OnDeleteClickListener {
        fun onDeleteClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener) {
        this.itemClickListener = listener
    }

    fun setOnDeleteClickListener(listener: OnDeleteClickListener) {
        this.deleteClickListener = listener
    }
}
