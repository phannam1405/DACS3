package com.example.dacs3.ui.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.PopupMenu
import com.example.dacs3.data.model.DataPlaylistDad
import com.example.dacs3.R

class PlaylistDadAdapter(
    private val activity: Activity,
    private var list: List<DataPlaylistDad>
) : ArrayAdapter<DataPlaylistDad>(activity, R.layout.custom_playlist_dad_list, list) {

    private var itemClickListener: OnItemClickListener? = null

    // ViewHolder pattern to improve performance
    private class ViewHolder(view: View) {
        val imageView: ImageView = view.findViewById(R.id.imgList)
        val titleView: TextView = view.findViewById(R.id.txtListTitle)
        val moreButton: ImageView = view.findViewById(R.id.btnMore)
    }

    override fun getCount(): Int = list.size

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val holder: ViewHolder
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

        // Set item click listener
        rowView.setOnClickListener {
            itemClickListener?.onItemClick(position)
        }


        holder.moreButton.setOnClickListener { v ->
            val popupMenu = PopupMenu(activity, v)
            popupMenu.menuInflater.inflate(R.menu.menu_playlist_options, popupMenu.menu)


            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_edit -> {
                        itemClickListener?.onEditNameClick(position)
                        true
                    }
                    R.id.menu_delete -> {
                        itemClickListener?.onDeleteClick(position)
                        true
                    }
                    else -> false
                }
            }

            popupMenu.show()
        }

        return rowView
    }

    // Method to update the list data
    fun updateData(newList: List<DataPlaylistDad>) {
        list = newList
        notifyDataSetChanged() // Notify that the data has changed
    }

    // Interface for item click listener
    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun onDeleteClick(position: Int)
        fun onEditNameClick(position: Int)
    }

    // Method to set the item click listener
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.itemClickListener = listener
    }
}
