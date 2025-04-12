package com.example.dacs3.ui.view

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.dacs3.databinding.ActivityPlayerBinding
import com.example.dacs3.ui.viewmodel.PlayerViewModel
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.media.audiofx.Visualizer
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.Toast
import com.example.dacs3.R
import com.example.dacs3.data.model.OutdataSongList
import com.example.dacs3.ui.viewmodel.FavouriteViewModel
import com.example.dacs3.ui.viewmodel.PlaylistChildViewModel


class PlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding
    private val viewModel: PlayerViewModel by viewModels()
    private lateinit var rotateAnimator: ObjectAnimator
    private val playlistViewModel: PlaylistChildViewModel by viewModels()
    private val favViewModel: FavouriteViewModel by viewModels()
    private var visualizer: Visualizer? = null

    override fun onCreate(savedInstanceState: Bundle?) {



        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbarInclude.btnReturn.setOnClickListener{
            finish()
        }


        // Xoay đĩa nhạc
        rotateAnimator = ObjectAnimator.ofFloat(binding.imgSong, View.ROTATION, 0f, 360f).apply {
            duration = 4000L
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
        }



        val imageSong = intent.getStringExtra("image")
        val audio = intent.getStringExtra("audio")
        //val uri = intent.getStringExtra("uri")
        val name = intent.getStringExtra("song_name")
        val song = intent.getSerializableExtra("song") as? OutdataSongList

        val songId = intent.getStringExtra("song_id")

        //check xem đã tim chưa để set tim đầy hoặc rỗng

        songId?.let { id ->
            favViewModel.isSongFavourite(id) { isFav ->
                var isCurrentlyFav = isFav
                updateHeartIcon(isCurrentlyFav)

                binding.toolbarInclude.btnHeart.setOnClickListener {
                    favViewModel.toggleFavourite(id)
                    isCurrentlyFav = !isCurrentlyFav
                    updateHeartIcon(isCurrentlyFav)
                }
            }
        }




        binding.songName.text = name
        Glide.with(this).load(imageSong).into(binding.imgSong)

        viewModel.prepareMediaPlayer(audio)

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
                rotateAnimator.pause()
            } else {
                viewModel.play()
                if (!rotateAnimator.isStarted) {
                    rotateAnimator.start()
                } else {
                    rotateAnimator.resume()
                }
            }
        }


        binding.seekBar.setOnSeekBarChangeListener(object : android.widget.SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: android.widget.SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) viewModel.seekTo(progress)
            }
            override fun onStartTrackingTouch(seekBar: android.widget.SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: android.widget.SeekBar?) {}
        })

        binding.btnInstall.setOnClickListener {
            song?.let {
                binding.btnInstall.isEnabled = false
                viewModel.downloadSong(it) { success ->
                    if (success) {
                        Toast.makeText(this, "Tải xuống thành công", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Tải xuống thất bại", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.btnPlaylist.setOnClickListener {
            song?.id?.let {
                playlistViewModel.loadPlaylistsDad()
                playlistViewModel.showAddSongDialog(this@PlayerActivity, it)
            }
        }

    }

    private fun updateHeartIcon(isFav: Boolean) {
        val iconRes = if (isFav) {
            R.drawable.heart_fill_svgrepo_com
        } else {
            R.drawable.icon_heart
        }
        binding.toolbarInclude.btnHeart.setImageResource(iconRes)
    }



    override fun onDestroy() {
        super.onDestroy()
        viewModel.releaseMediaPlayer()
        visualizer?.release()
        visualizer = null
    }


    private fun formatTime(millis: Int): String {
        val minutes = (millis / 1000) / 60
        val seconds = (millis / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

}
