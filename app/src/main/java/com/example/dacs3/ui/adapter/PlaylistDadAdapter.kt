package com.example.dacs3.ui.adapter

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.dacs3.data.model.OutdataPlaylistDad
import com.example.dacs3.R

class PlaylistDadAdapter(val activity: Activity, val list: List<OutdataPlaylistDad>) : ArrayAdapter<OutdataPlaylistDad>(activity,
    R.layout.custom_playlist_dad_list, list)
 {
    override fun getCount(): Int {
        return list.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val context = activity.layoutInflater
        val rowView = context.inflate(R.layout.custom_playlist_dad_list,parent, false)
        val images = rowView.findViewById<ImageView>(R.id.imgList)
        val title = rowView.findViewById<TextView>(R.id.txtListTitle)

        images.setImageResource(list[position].image)
        title.text = list[position].title

        return rowView
    }
}