package com.example.dacs3

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

class Adapter_Playlist_Dad(val activity: Activity, val list: List<Outdata_Playlist_Dad>) : ArrayAdapter<Outdata_Playlist_Dad>(activity, R.layout.custom_playlist_dad_list, list)
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