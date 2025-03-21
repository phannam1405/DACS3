package com.example.dacs3

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

class Adapter_Playlist_Child(val activity: Activity, val list: List<Outdata_Playlist_Child>): ArrayAdapter<Outdata_Playlist_Child>(activity, R.layout.custom_playlist_child_list) {

    override fun getCount(): Int {
        return list.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        // Chuyen xml thanh giao dien
        val context = activity.layoutInflater

        val rowView = context.inflate(R.layout.custom_playlist_child_list, null)

        val images = rowView.findViewById<ImageView>(R.id.imgMusic)
        val title = rowView.findViewById<TextView>(R.id.txtTitle)
        val singer = rowView.findViewById<TextView>(R.id.txtSinger)
        val time = rowView.findViewById<TextView>(R.id.txtTime)

        title.text = list[position].title
        singer.text = list[position].singer
        time.text = list[position].time
        images.setImageResource(list[position].image)

        return rowView
    }
}