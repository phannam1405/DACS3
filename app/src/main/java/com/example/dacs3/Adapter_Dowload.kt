package com.example.dacs3

import android.app.Activity
import android.app.AlertDialog
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import java.io.File

class Adapter_Dowload(
    val activity: Activity,
    val list: MutableList<Music>,
    private val listener: OnItemClickListener // Truyền listener vào
) : ArrayAdapter<Music>(activity, R.layout.custom_down) {

    interface OnItemClickListener { // Đổi tên đúng chuẩn
        fun ngheNhacOffline(position: Int)
    }

    override fun getCount(): Int = list.size

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val context = activity.layoutInflater
        val rowView = convertView ?: context.inflate(R.layout.custom_down, parent, false)

        val images = rowView.findViewById<ImageView>(R.id.imgMusic)
        val title = rowView.findViewById<TextView>(R.id.txtTitle)
        val singer = rowView.findViewById<TextView>(R.id.txtSinger)
        val time = rowView.findViewById<TextView>(R.id.txtTime)
        val btnDelete = rowView.findViewById<ImageView>(R.id.btnDelete)

        val song = list[position]

        title.text = song.songName
        singer.text = song.singer_name ?: "Unknown Singer"
        time.text = getAudioDuration(song.localAudioPath)

        // Dùng Glide để load ảnh
        Glide.with(activity)
            .load(song.coverImage)
            .placeholder(R.drawable.placeholder) // Ảnh mặc định khi tải
            .error(R.drawable.error) // Ảnh hiển thị khi lỗi
            .into(images)

        btnDelete.setOnClickListener {
            val song = list[position]

            AlertDialog.Builder(activity)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa bài hát '${song.songName}' không?")
                .setPositiveButton("Xóa") { _, _ ->
                    Log.d("DeleteTest", "Bài hát cần xóa: ID = ${song.id}, Name = ${song.songName}")

                    // Xóa khỏi database
                    val musicRepository = MusicRepository(activity.application)
                    val deletedRows = musicRepository.deleteMusic(song)


                    if (deletedRows > 0) {
                        // Xóa khỏi danh sách hiển thị
                        list.removeAt(position)
                        notifyDataSetChanged()
                        Toast.makeText(activity, "Đã xóa ${song.songName} khỏi database", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(activity, "Không thể xóa bài hát", Toast.LENGTH_SHORT).show()
                    }

                    // Load lại danh sách từ database
                    list.clear()
                    val newList = musicRepository.getAll()?.filterNotNull() ?: emptyList()
                    Log.d("DeleteTest", "Danh sách sau khi load lại: ${newList.size} bài hát")
                    list.addAll(newList)
                    notifyDataSetChanged()
                }
                .setNegativeButton("Hủy", null)
                .show()
        }





        rowView.setOnClickListener {
            listener.ngheNhacOffline(position)
        }

        return rowView
    }


    private fun getAudioDuration(audioPath: String?): String {
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
}
