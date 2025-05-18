package com.example.dacs3.ui.view

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import com.example.dacs3.R
import com.example.dacs3.data.model.DataSongList
import com.example.dacs3.databinding.ActivityPlayerBinding
import com.example.dacs3.ui.viewmodel.FavouriteViewModel
import com.example.dacs3.ui.viewmodel.PlayerViewModel
import com.example.dacs3.ui.viewmodel.PlaylistChildViewModel
import java.net.URL

class PlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding
    private val viewModel: PlayerViewModel by viewModels()
    private val playlistViewModel: PlaylistChildViewModel by viewModels()
    private val favViewModel: FavouriteViewModel by viewModels()
    private lateinit var rotateAnimator: ObjectAnimator

    private val callReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.getStringExtra(TelephonyManager.EXTRA_STATE)) {
                TelephonyManager.EXTRA_STATE_RINGING -> {
                    if (viewModel.isPlaying.value == true) {
                        viewModel.pause()
                        binding.btnPlayPause.setIconResource(R.drawable.icon_play)
                        rotateAnimator.pause()
                    }
                }
                TelephonyManager.EXTRA_STATE_IDLE -> {
                    if (viewModel.isPlaying.value == false && viewModel.wasPlayingBeforeCall) {
                        viewModel.play()
                        binding.btnPlayPause.setIconResource(R.drawable.icon_pause)
                        rotateAnimator.resume()
                    }
                }
            }
        }
    }

    companion object {
        const val ACTION_CLOSE = "action_close"
        const val NOTIFICATION_ID = 101
        const val CHANNEL_ID = "music_channel"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRotationAnimator()
        setupSongData()
        setupPlayerControls()
        setupDownloadButton()
        setupPlaylistButton()
        createNotificationChannel()

        when (intent?.action) {
            ACTION_CLOSE -> {
                viewModel.releaseMediaPlayer()
                removeNotification()
                finish()
            }
        }

        val songsList = intent.getSerializableExtra("song_list") as? ArrayList<DataSongList>
        val currentSong = intent.getSerializableExtra("song") as? DataSongList

        if (songsList != null && currentSong != null) {
            viewModel.setSongsList(songsList)
            val currentIndex = songsList.indexOfFirst { it.id == currentSong.id }
            viewModel.setCurrentSongIndex(currentIndex)
            playSong(currentSong)
        }

        val filter = IntentFilter(TelephonyManager.ACTION_PHONE_STATE_CHANGED)
        registerReceiver(callReceiver, filter)

        viewModel.setupAudioFocus()
    }

    private fun setupToolbar() {
        binding.toolbarInclude.btnReturn.setOnClickListener {
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
    }

    private fun setupRotationAnimator() {
        rotateAnimator = ObjectAnimator.ofFloat(binding.imgSong, View.ROTATION, 0f, 360f).apply {
            duration = 4000L
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
        }
    }

    private fun setupSongData() {
        val imageSong = intent.getStringExtra("image")
        val audio = intent.getStringExtra("audio")
        val name = intent.getStringExtra("song_name")
        val songId = intent.getStringExtra("song_id")
        binding.songName.text = name
        Glide.with(this).load(imageSong).into(binding.imgSong)

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

        viewModel.prepareMediaPlayer(audio)
        if (intent?.getBooleanExtra("EXIT", false) == true) {
            finish()
            return
        }
    }

    private fun setupPlayerControls() {
        viewModel.duration.observe(this) { duration ->
            binding.seekBar.max = duration
            binding.endTime.text = formatTime(duration)
        }

        viewModel.currentPosition.observe(this) { position ->
            binding.seekBar.progress = position
            binding.startTime.text = formatTime(position)
        }

        binding.btnPlayPause.setOnClickListener {
            if (viewModel.isPlaying.value == true) {
                viewModel.pause()
                rotateAnimator.pause()
                binding.btnPlayPause.setIconResource(R.drawable.icon_play)
            } else {
                viewModel.play()
                if (!rotateAnimator.isStarted) {
                    rotateAnimator.start()
                } else {
                    rotateAnimator.resume()
                }
                binding.btnPlayPause.setIconResource(R.drawable.icon_pause)
            }
        }

        binding.btnNextSong.setOnClickListener {
            playNextSong()
        }

        binding.btnPreSong.setOnClickListener {
            playPreviousSong()
        }

        binding.seekBar.setOnSeekBarChangeListener(object : android.widget.SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: android.widget.SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) viewModel.seekTo(progress)
            }

            override fun onStartTrackingTouch(seekBar: android.widget.SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: android.widget.SeekBar?) {}
        })
    }

    private fun setupDownloadButton() {
        val song = intent.getSerializableExtra("song") as? DataSongList

        binding.btnInstall.setOnClickListener {
            song?.let {
                binding.btnInstall.isEnabled = false
                viewModel.downloadSong(it) { success ->
                    Toast.makeText(
                        this,
                        if (success) "Tải xuống thành công" else "Tải xuống thất bại",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun setupPlaylistButton() {
        val song = intent.getSerializableExtra("song") as? DataSongList

        binding.btnPlaylist.setOnClickListener {
            song?.id?.let { songId ->
                playlistViewModel.loadPlaylistsDad()
                playlistViewModel.showAddSongDialog(this@PlayerActivity, songId)
            }
        }
    }

    private fun updateHeartIcon(isFav: Boolean) {
        val iconRes = if (isFav) R.drawable.heart_fill_svgrepo_com else R.drawable.icon_heart
        binding.toolbarInclude.btnHeart.setImageResource(iconRes)
    }

    private fun formatTime(millis: Int): String {
        val minutes = (millis / 1000) / 60
        val seconds = (millis / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun playNextSong() {
        val songs = viewModel.getSongsList()
        if (songs.isEmpty()) return

        val nextIndex = (viewModel.getCurrentSongIndex() + 1) % songs.size
        playSong(songs[nextIndex])
        viewModel.setCurrentSongIndex(nextIndex)
    }

    private fun playPreviousSong() {
        val songs = viewModel.getSongsList()
        if (songs.isEmpty()) return

        val prevIndex = (viewModel.getCurrentSongIndex() - 1 + songs.size) % songs.size
        playSong(songs[prevIndex])
        viewModel.setCurrentSongIndex(prevIndex)
    }

    private fun playSong(song: DataSongList) {
        viewModel.releaseMediaPlayer()
        binding.songName.text = song.songName
        Glide.with(this).load(song.image).into(binding.imgSong)
        rotateAnimator.pause()

        song.id?.let { id ->
            favViewModel.isSongFavourite(id) { isFav ->
                updateHeartIcon(isFav)
                binding.toolbarInclude.btnHeart.setOnClickListener {
                    favViewModel.toggleFavourite(id)
                    updateHeartIcon(!isFav)
                }
            }
        }
        viewModel.prepareMediaPlayer(song.audio)
        viewModel.play()
        showNotification(song)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Trình phát nhạc",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Điều khiển phát nhạc"
                setSound(null, null)
            }

            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(song: DataSongList) {
        val closeIntent = Intent(this, NotificationReceiver::class.java).apply {
            action = ACTION_CLOSE
        }

        val pendingClose = PendingIntent.getBroadcast(
            this, 4, closeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(song.songName ?: "Bài hát không xác định")
            .setContentText(song.singerName ?: "Nghệ sĩ không xác định")
            .setSmallIcon(R.drawable.logo_app)
            .setLargeIcon(getBitmapFromUrl(song.image))
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .addAction(R.drawable.icon_logout, "Đóng", pendingClose)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle()// Hiển thị action đầu tiên (index 0) ở chế độ compact
                .setShowActionsInCompactView(0)
            )
            .build()

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun getBitmapFromUrl(url: String?): Bitmap? {
        if (url.isNullOrEmpty()) return null
        return try {
            val inputStream = URL(url).openStream()
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            null
        }
    }

    private fun removeNotification() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_ID)
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(callReceiver)
        } catch (e: IllegalArgumentException) {
        }

        if (!isChangingConfigurations) {
            viewModel.releaseMediaPlayer()
            removeNotification()
        }
    }
}