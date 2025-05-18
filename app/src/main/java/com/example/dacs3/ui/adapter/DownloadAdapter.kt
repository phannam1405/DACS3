package com.example.dacs3.ui.adapter

import android.app.AlertDialog
import android.media.MediaMetadataRetriever
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.dacs3.R
import com.example.dacs3.data.model.Music
import com.example.dacs3.data.repository.MusicRepository

class DownloadAdapter(
    val activity: AppCompatActivity,
    val list: MutableList<Music>,
    private val listener: OnItemClickListener
) : ArrayAdapter<Music>(activity, R.layout.custom_down) {

    private val musicRepository = MusicRepository(activity.application)

    override fun getCount(): Int = list.size

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val rowView = convertView ?: LayoutInflater.from(activity).inflate(R.layout.custom_down, parent, false)
        val images = rowView.findViewById<ImageView>(R.id.imgMusic)
        val title = rowView.findViewById<TextView>(R.id.txtTitle)
        val singer = rowView.findViewById<TextView>(R.id.txtSinger)
        val time = rowView.findViewById<TextView>(R.id.txtTime)
        val btnDelete = rowView.findViewById<ImageView>(R.id.btnDelete)
        val song = list[position]

        title.text = song.songName
        singer.text = song.singer_name
        time.text = getAudioDuration(song.localAudioPath)

        Glide.with(activity)
            .load(song.coverImage)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.error)
            .into(images)

        btnDelete.setOnClickListener {
            AlertDialog.Builder(activity).apply {
                setTitle("Xác nhận xóa")
                setMessage("Bạn có chắc chắn muốn xóa bài hát '${song.songName}' không?")
                setPositiveButton("Xóa") { _, _ ->
                    Log.d("DeleteTest", "Bài hát cần xóa: ID = ${song.id}, Name = ${song.songName}")

                    // Xóa khỏi database
                    val deletedRows = musicRepository.deleteMusic(song)

                    if (deletedRows > 0) {
                        Toast.makeText(activity, "Đã xóa ${song.songName} khỏi database", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(activity, "Không thể xóa bài hát", Toast.LENGTH_SHORT).show()
                    }

                    list.clear()
                    list.addAll(musicRepository.getAll()?.filterNotNull() ?: emptyList())

                    notifyDataSetChanged()
                }
                setNegativeButton("Hủy", null)
            }.show()
        }

        rowView.setOnClickListener {
            listener.ngheNhacOffline(position)
        }

        return rowView
    }

    fun getAudioDuration(audioPath: String?): String {
        if (audioPath.isNullOrEmpty()) return "00:00"
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(audioPath)
            val durationMs = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 0L
            retriever.release()
            val minutes = (durationMs / 1000) / 60
            val seconds = (durationMs / 1000) % 60
            String.format("%02d:%02d", minutes, seconds)
        } catch (e: Exception) {
            e.printStackTrace()
            "00:00"
        }
    }

    interface OnItemClickListener {
        fun ngheNhacOffline(position: Int)
    }
}
