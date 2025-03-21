package com.example.dacs3

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.dacs3.databinding.ActivityPlayerBinding
import java.io.IOException

class PlayerActivity : AppCompatActivity() {
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var binding: ActivityPlayerBinding
    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageSong = intent.getStringExtra("image")
        val audio = intent.getStringExtra("audio")

        // Load ảnh bài hát
        Glide.with(this)
            .load(imageSong)
            .into(binding.imgSong)

        mediaPlayer = MediaPlayer()
        try {
            mediaPlayer?.apply {
                if (audio?.startsWith("http") == true) {
                    // Phát nhạc online từ URL
                    setDataSource(audio)
                } else {
                    // Phát nhạc offline từ bộ nhớ
                    setDataSource(this@PlayerActivity, Uri.parse(audio))
                }
                prepareAsync()
                setOnPreparedListener {
                    start()
                    binding.seekBar.max = duration
                    binding.endTime.text = formatTime(duration)
                    updateSeekBar()
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

    // Cập nhật SeekBar liên tục
    private fun updateSeekBar() {
        mediaPlayer?.let {
            binding.seekBar.progress = it.currentPosition
            binding.startTime.text = formatTime(it.currentPosition)

            if (it.isPlaying) {
                handler.postDelayed({ updateSeekBar() }, 1000)
            }
        }
    }

    // Định dạng thời gian mm:ss
    private fun formatTime(millis: Int): String {
        val minutes = (millis / 1000) / 60
        val seconds = (millis / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
