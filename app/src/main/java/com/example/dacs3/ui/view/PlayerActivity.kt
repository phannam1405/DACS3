package com.example.dacs3.ui.view

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.dacs3.databinding.ActivityPlayerBinding
import com.example.dacs3.ui.viewmodel.MainViewModel
import com.example.dacs3.ui.viewmodel.PlayerViewModel
import com.example.dacs3.ui.viewmodel.PlaylistChildViewModel

class PlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding
    private val viewModel: PlayerViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val imageSong = intent.getStringExtra("image")
        val audio = intent.getStringExtra("audio")
        val uri = intent.getStringExtra("uri")
        val songId = intent.getStringExtra("song_id")

        Glide.with(this).load(imageSong).into(binding.imgSong)

        Log.d("PlayerActivity", "Audio Path: $audio")

        viewModel.prepareMediaPlayer(audio, uri)

        viewModel.duration.observe(this) {
            binding.seekBar.max = it
            binding.endTime.text = formatTime(it)
        }

        viewModel.currentPosition.observe(this) {
            binding.seekBar.progress = it
            binding.startTime.text = formatTime(it)
        }

        viewModel.isPlaying.observe(this) { isPlaying ->
            binding.btnPlayPause.text = if (isPlaying) "Pause" else "Play"
        }

        binding.btnPlayPause.setOnClickListener {
            if (viewModel.isPlaying.value == true) {
                viewModel.pause()
            } else {
                viewModel.play()
            }
        }

        binding.seekBar.setOnSeekBarChangeListener(object : android.widget.SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: android.widget.SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) viewModel.seekTo(progress)
            }
            override fun onStartTrackingTouch(seekBar: android.widget.SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: android.widget.SeekBar?) {}
        })


    }




    override fun onDestroy() {
        super.onDestroy()
        viewModel.releaseMediaPlayer()
    }

    private fun formatTime(millis: Int): String {
        val minutes = (millis / 1000) / 60
        val seconds = (millis / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}
