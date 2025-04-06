package com.example.dacs3.ui.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.dacs3.data.model.OutdataSongList
import com.example.dacs3.R

class PlaylistChildAdapter(val activity: Activity, var list: List<OutdataSongList>) : ArrayAdapter<OutdataSongList>(activity,
    R.layout.custom_playlist_child_list, list) {

    private var itemClickListener: onItemClickListener? = null

    // ViewHolder pattern to improve performance
    private class ViewHolder(view: View) {
        val imageView: ImageView = view.findViewById(R.id.imgMusic)
        val titleView: TextView = view.findViewById(R.id.txtTitle)
        val singerView: TextView = view.findViewById(R.id.txtSinger)
        val timeView: TextView = view.findViewById(R.id.txtTime)
    }

    override fun getCount(): Int {
        return list.size
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val holder: ViewHolder

        // Reuse or create a new row view
        val rowView: View = if (convertView == null) {
            val inflater = activity.layoutInflater
            val view = inflater.inflate(R.layout.custom_playlist_child_list, parent, false)
            holder = ViewHolder(view)
            view.tag = holder
            view
        } else {
            holder = convertView.tag as ViewHolder
            convertView
        }

        // Set image, title, singer, and time for the current row
        val currentItem = list[position]
        Glide.with(activity)
            .load(list[position].image)  // URL của ảnh
            .into(holder.imageView)

        holder.titleView.text = currentItem.song_name
        holder.singerView.text = currentItem.singer_name
        holder.timeView.text = currentItem.cate

        // Set on click listener for the item
        rowView.setOnClickListener {
            itemClickListener?.onItemClick(position)
        }

        return rowView
    }

    // Method to update the list data
    fun updateData(newList: List<OutdataSongList>) {
        list = newList
        Log.d("PlaylistChildAdapter", "List data updated, size: ${list.size}")
        notifyDataSetChanged() // Notify that the data has changed
    }

    // Interface for item click listener
    interface onItemClickListener {
        fun onItemClick(position: Int)
    }

    // Method to set the item click listener
    fun setOnItemClickListener(listener: onItemClickListener) {
        this.itemClickListener = listener
    }
}
