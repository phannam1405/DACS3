package com.example.dacs3

import android.app.Activity
import android.net.Uri
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import java.io.File

class Dowload_Adapter(val activity: Activity, val list: List<Music>) :
    ArrayAdapter<Music>(activity, R.layout.custom_down) {

    override fun getCount(): Int = list.size // Trả về số lượng bài hát đã tải

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val context = activity.layoutInflater
        val rowView = convertView ?: context.inflate(R.layout.custom_down, parent, false)

        // Ánh xạ các View
        val images = rowView.findViewById<ImageView>(R.id.imgMusic)
        val title = rowView.findViewById<TextView>(R.id.txtTitle)
        val singer = rowView.findViewById<TextView>(R.id.txtSinger)
        val time = rowView.findViewById<TextView>(R.id.txtTime)

        // Lấy dữ liệu của bài hát hiện tại
        val song = list[position]

        // Gán dữ liệu vào View
        title.text = song.songName
        singer.text = song.singer_name ?: "Unknown Singer"
        time.text = "3:45" // Cần lấy thời gian thực từ file mp3

        // Log kiểm tra dữ liệu của bài hát này
        Log.d("DatabaseQuery", "Song Name: ${song.songName}")
        Log.d("DatabaseQuery", "Cover Image Path: ${song.coverImage}")
        Log.d("DatabaseQuery", "Singer Image Path: ${song.singerImage}")
        Log.d("DatabaseQuery", "Audio File Path: ${song.localAudioPath}")

        // Kiểm tra và load ảnh
        loadImage(song.coverImage, images)

        return rowView
    }

    // Hàm kiểm tra và tải ảnh
    private fun loadImage(imagePath: String?, imageView: ImageView) {
        if (!imagePath.isNullOrEmpty()) {
            val imageFile = File(imagePath)
            if (imageFile.exists()) {
                Glide.with(activity)
                    .load(Uri.fromFile(imageFile)) // Dùng Uri để đọc file local
                    .placeholder(R.drawable.error)
                    .into(imageView)
            } else {
                Log.e("GlideError", "File không tồn tại: ${imageFile.absolutePath}")
                setErrorImage(imageView)
            }
        } else {
            Log.e("GlideError", "Đường dẫn ảnh bị null hoặc rỗng")
            setErrorImage(imageView)
        }
    }

    // Hàm đặt ảnh lỗi
    private fun setErrorImage(imageView: ImageView) {
        imageView.setImageResource(R.drawable.error)
    }
}
