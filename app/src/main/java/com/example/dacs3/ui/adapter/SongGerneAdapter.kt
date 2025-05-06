package com.example.dacs3.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.dacs3.R
import com.example.dacs3.data.model.DataSongList

class SongGerneAdapter(
    private val context: Context,
    private var songList: List<DataSongList>
) : BaseAdapter() {

    interface OnItemClickListener {
        fun onItemClick(song: DataSongList)
    }

    private var listener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun getCount(): Int = songList.size

    override fun getItem(position: Int): Any = songList[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.custom_musicgerne, parent, false)
        }

        val song = getItem(position) as DataSongList

        val txtTitle = view?.findViewById<TextView>(R.id.txtTitle)
        val txtSinger = view?.findViewById<TextView>(R.id.txtSinger)
        val txtTime = view?.findViewById<TextView>(R.id.txtTime)
        val imgMusic = view?.findViewById<ImageView>(R.id.imgMusic)

        txtTitle?.text = song.songName
        txtSinger?.text = song.singerName
        txtTime?.text = "4:30"

        Glide.with(context)
            .load(song.image)
            .into(imgMusic!!)

        view?.setOnClickListener {
            listener?.onItemClick(song)
        }

        return view!!
    }
}
