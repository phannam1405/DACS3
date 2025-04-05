package com.example.dacs3.ui.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

import com.example.dacs3.data.model.OutdataPlaylistDad
import com.example.dacs3.R

class PlaylistDadAdapter(val activity: Activity, var list: List<OutdataPlaylistDad>) : ArrayAdapter<OutdataPlaylistDad>(activity,
    R.layout.custom_playlist_dad_list, list) {

    private var itemClickListener: onItemClickListenner? = null

    // ViewHolder pattern to improve performance
    private class ViewHolder(view: View) {
        val imageView: ImageView = view.findViewById(R.id.imgList)
        val titleView: TextView = view.findViewById(R.id.txtListTitle)
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
            val view = inflater.inflate(R.layout.custom_playlist_dad_list, parent, false)
            holder = ViewHolder(view)
            view.tag = holder
            view
        } else {
            holder = convertView.tag as ViewHolder
            convertView
        }

        // Set image and title for the current row
        val currentItem = list[position]
        holder.imageView.setImageResource(R.drawable.singer_minh_gay)
        holder.titleView.text = currentItem.title

        // Set on click listener for the item
        rowView.setOnClickListener {
            itemClickListener?.onItemClick(position)
        }

        return rowView
    }

    // Method to update the list data
    fun updateData(newList: List<OutdataPlaylistDad>) {
        list = newList
        notifyDataSetChanged() // Notify that the data has changed
    }

    // Interface for item click listener
    interface onItemClickListenner {
        fun onItemClick(position: Int)
    }

    // Method to set the item click listener
    fun setOnItemClickListenner(listener: onItemClickListenner) {
        this.itemClickListener = listener
    }
}

