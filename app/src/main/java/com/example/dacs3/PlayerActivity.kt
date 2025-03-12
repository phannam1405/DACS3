package com.example.dacs3

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.dacs3.databinding.ActivityPlayerBinding
import java.io.IOException

class PlayerActivity : AppCompatActivity() {
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var binding: ActivityPlayerBinding
    private val musicUrl = "https://res.cloudinary.com/dl1exacvx/video/upload/v1741794541/audio_test_t77jxj.mp3"
    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mediaPlayer = MediaPlayer()
        try {
            mediaPlayer?.apply {
                setDataSource(musicUrl) // Lấy nhạc từ Cloudinary
                prepareAsync() // Chuẩn bị phát nhạc (không chặn UI)
                setOnPreparedListener {
                    start()
                    binding.seekBar.max = duration // Cập nhật max cho SeekBar
                    binding.endTime.text = formatTime(duration) // Hiển thị tổng thời gian
                    updateSeekBar() // Bắt đầu cập nhật SeekBar
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        // Xử lý khi kéo SeekBar
        binding.seekBar.setOnSeekBarChangeListener(object : android.widget.SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: android.widget.SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer?.seekTo(progress)
                    binding.startTime.text = formatTime(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: android.widget.SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: android.widget.SeekBar?) {}
        })
    }

    // Hàm cập nhật SeekBar liên tục
    private fun updateSeekBar() {
        mediaPlayer?.let {
            binding.seekBar.progress = it.currentPosition
            binding.startTime.text = formatTime(it.currentPosition)

            if (it.isPlaying) {
                handler.postDelayed({ updateSeekBar() }, 1000) // Cập nhật mỗi giây
            }
        }
    }

    // Hàm định dạng thời gian mm:ss
    private fun formatTime(millis: Int): String {
        val minutes = (millis / 1000) / 60
        val seconds = (millis / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release() // Giải phóng MediaPlayer khi thoát
        mediaPlayer = null
    }
}
