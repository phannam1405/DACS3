package com.example.dacs3.ui.adapter

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.dacs3.data.model.OutdataPlaylistChild
import com.example.dacs3.R

class PlaylistChildAdapter(val activity: Activity, val list: List<OutdataPlaylistChild>): ArrayAdapter<OutdataPlaylistChild>(activity,
    R.layout.custom_playlist_child_list
) {

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