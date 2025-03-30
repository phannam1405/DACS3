package com.example.dacs3.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.dacs3.R
import com.example.dacs3.data.model.OutdataFav

class FavouriteAdapter(
    private val activity: AppCompatActivity,
    private val list: List<OutdataFav>
) : ArrayAdapter<OutdataFav>(activity, R.layout.custom_favourite_list) {

    override fun getCount(): Int = list.size

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val rowView = convertView ?: LayoutInflater.from(activity).inflate(
            R.layout.custom_favourite_list, parent, false
        )

        val images = rowView.findViewById<ImageView>(R.id.imgMusic)
        val title = rowView.findViewById<TextView>(R.id.txtTitle)
        val singer = rowView.findViewById<TextView>(R.id.txtSinger)
        val time = rowView.findViewById<TextView>(R.id.txtTime)

        val song = list[position]
        title.text = song.title
        singer.text = song.singer
        time.text = song.time
        images.setImageResource(song.image)

        return rowView
    }
}
