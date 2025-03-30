package com.example.dacs3.ui.adapter

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.dacs3.R
import com.example.dacs3.data.model.Music
import com.example.dacs3.ui.viewmodel.DownloadViewModel

class DownloadAdapter(
    val activity: AppCompatActivity,  // Sửa thành AppCompatActivity
    val list: MutableList<Music>,
    private val listener: OnItemClickListener
) : ArrayAdapter<Music>(activity, R.layout.custom_down) {

    interface OnItemClickListener {
        fun ngheNhacOffline(position: Int)
    }

    private val viewModel: DownloadViewModel = ViewModelProvider(activity)[DownloadViewModel::class.java]

    override fun getCount(): Int = list.size

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val context = activity.layoutInflater
        val rowView = convertView ?: LayoutInflater.from(activity).inflate(R.layout.custom_down, parent, false)

        val images = rowView.findViewById<ImageView>(R.id.imgMusic)
        val title = rowView.findViewById<TextView>(R.id.txtTitle)
        val singer = rowView.findViewById<TextView>(R.id.txtSinger)
        val time = rowView.findViewById<TextView>(R.id.txtTime)
        val btnDelete = rowView.findViewById<ImageView>(R.id.btnDelete)

        val song = list[position]

        title.text = song.songName
        singer.text = song.singer_name ?: "Unknown Singer"
        time.text = viewModel.getAudioDuration(song.localAudioPath)

        Glide.with(activity)
            .load(song.coverImage)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.error)
            .into(images)

        btnDelete.setOnClickListener {
            AlertDialog.Builder(activity)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa bài hát '${song.songName}' không?")
                .setPositiveButton("Xóa") { _, _ ->
                    viewModel.deleteSong(song, position, list) { success ->
                        if (success) {
                            list.removeAt(position)
                            notifyDataSetChanged()
                            Toast.makeText(activity, "Đã xóa ${song.songName}", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(activity, "Không thể xóa bài hát", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                .setNegativeButton("Hủy", null)
                .show()
        }

        rowView.setOnClickListener {
            listener.ngheNhacOffline(position)
        }

        return rowView
    }
}
