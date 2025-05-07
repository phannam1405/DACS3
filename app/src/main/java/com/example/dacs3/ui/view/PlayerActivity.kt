package com.example.dacs3.ui.view

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.media.audiofx.Visualizer
import android.os.Bundle
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.dacs3.R
import com.example.dacs3.data.model.DataSongList
import com.example.dacs3.databinding.ActivityPlayerBinding
import com.example.dacs3.ui.viewmodel.FavouriteViewModel
import com.example.dacs3.ui.viewmodel.PlayerViewModel
import com.example.dacs3.ui.viewmodel.PlaylistChildViewModel

class PlayerActivity : AppCompatActivity() {

    // View Binding để thao tác với layout
    private lateinit var binding: ActivityPlayerBinding

    // ViewModel điều khiển phát nhạc
    private val viewModel: PlayerViewModel by viewModels()

    // ViewModel quản lý danh sách phát
    private val playlistViewModel: PlaylistChildViewModel by viewModels()

    // ViewModel quản lý bài hát yêu thích
    private val favViewModel: FavouriteViewModel by viewModels()

    // Animation xoay ảnh đĩa
    private lateinit var rotateAnimator: ObjectAnimator

    // Hàm khởi tạo Activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Khởi tạo Toolbar
        setupToolbar()

        // Khởi tạo animation xoay ảnh
        setupRotationAnimator()

        // Khởi tạo dữ liệu bài hát
        setupSongData()

        // Khởi tạo các điều khiển phát nhạc
        setupPlayerControls()

        // Khởi tạo nút tải bài hát về thiết bị
        setupDownloadButton()

        // Khởi tạo nút thêm bài hát vào danh sách phát
        setupPlaylistButton()

        val songsList = intent.getSerializableExtra("song_list") as? ArrayList<DataSongList>
        val currentSong = intent.getSerializableExtra("song") as? DataSongList


        if (songsList != null && currentSong != null) {
            // Lưu danh sách vào ViewModel
            viewModel.setSongsList(songsList)

            // Tìm vị trí bài hát hiện tại trong danh sách
            val currentIndex = songsList.indexOfFirst { it.id == currentSong.id }
            viewModel.setCurrentSongIndex(currentIndex)

            // Phát bài hát
            playSong(currentSong)
        }


        if (songsList != null) {
            viewModel.setSongsList(songsList)
            // Tìm vị trí bài hát hiện tại trong playlist
            val currentSong = intent.getSerializableExtra("song") as? DataSongList
            val currentIndex = songsList.indexOfFirst { it.id == currentSong?.id }
            if (currentIndex != -1) {
                viewModel.setCurrentSongIndex(currentIndex)
            }
        }
    }



    // Xử lý nút trở về trong Toolbar
    private fun setupToolbar() {
        binding.toolbarInclude.btnReturn.setOnClickListener {
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
    }






    // Thiết lập animation xoay cho ảnh bài hát
    private fun setupRotationAnimator() {
        rotateAnimator = ObjectAnimator.ofFloat(binding.imgSong, View.ROTATION, 0f, 360f).apply {
            duration = 4000L
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
        }
    }



    // Hàm khởi tạo dữ liệu bài hát
    private fun setupSongData() {
        val imageSong = intent.getStringExtra("image")
        val audio = intent.getStringExtra("audio")
        val name = intent.getStringExtra("song_name")
        val songId = intent.getStringExtra("song_id")
        binding.songName.text = name
        Glide.with(this).load(imageSong).into(binding.imgSong)

        // Xử lý trạng thái yêu thích
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
    }







    // Hàm xử lý các nút tương tác với các button trong player
    private fun setupPlayerControls() {
        viewModel.duration.observe(this) { duration ->
            binding.seekBar.max = duration
            binding.endTime.text = formatTime(duration)
        }

        viewModel.currentPosition.observe(this) { position ->
            binding.seekBar.progress = position
            binding.startTime.text = formatTime(position)
        }

        viewModel.isPlaying.observe(this) { isPlaying ->
            binding.btnPlayPause.text = if (isPlaying) "Pause" else "Play"
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

        binding.btnNextSong.setOnClickListener{
            playNextSong()
        }

        binding.btnPreSong.setOnClickListener{
            playPreviousSong()
        }

        // Xử lý kéo SeekBar để tua nhạc
        binding.seekBar.setOnSeekBarChangeListener(object : android.widget.SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: android.widget.SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) viewModel.seekTo(progress)
            }

            override fun onStartTrackingTouch(seekBar: android.widget.SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: android.widget.SeekBar?) {}
        })
    }






    // Hàm xử lý nút tải bài hát về thiết bị
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






    // Hàm khởi tạo nút thêm bài hát vào danh sách phát
    private fun setupPlaylistButton() {
        val song = intent.getSerializableExtra("song") as? DataSongList

        binding.btnPlaylist.setOnClickListener {
            song?.id?.let { songId ->
                playlistViewModel.loadPlaylistsDad()
                playlistViewModel.showAddSongDialog(this@PlayerActivity, songId)
            }
        }
    }




   //Hàm cập nhật biểu tượng yêu thích dựa trên trạng thái yêu thích
    private fun updateHeartIcon(isFav: Boolean) {
        val iconRes = if (isFav) R.drawable.heart_fill_svgrepo_com else R.drawable.icon_heart
        binding.toolbarInclude.btnHeart.setImageResource(iconRes)
    }




    // Hàm định dạng thơ gian của bài hát
    private fun formatTime(millis: Int): String {
        val minutes = (millis / 1000) / 60
        val seconds = (millis / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }


    // Hàm nhảy qua bài hát kế tiếp
    private fun playNextSong() {
        val songs = viewModel.getSongsList()
        if (songs.isEmpty()) return

        val nextIndex = when {
            // Nếu đang phát từ playlist và là bài cuối -> dừng
            viewModel.getSource() == "playlist" && viewModel.getCurrentSongIndex() == songs.size - 1 -> {
                Toast.makeText(this, "Đã hết playlist", Toast.LENGTH_SHORT).show()
                return
            }
            // Các trường hợp khác (từ song_list/search) -> chuyển vòng
            else -> (viewModel.getCurrentSongIndex() + 1) % songs.size
        }

        playSong(songs[nextIndex])
        viewModel.setCurrentSongIndex(nextIndex)
    }

    // Hàm nhảy qua bài hát trước đó
    private fun playPreviousSong() {
        val songs = viewModel.getSongsList()
        if (songs.isEmpty()) return

        val prevIndex = when {
            // Nếu đang phát từ playlist và là bài đầu -> dừng
            viewModel.getSource() == "playlist" && viewModel.getCurrentSongIndex() == 0 -> {
                Toast.makeText(this, "Đây là bài đầu tiên", Toast.LENGTH_SHORT).show()
                return
            }
            // Các trường hợp khác -> chuyển vòng
            else -> (viewModel.getCurrentSongIndex() - 1 + songs.size) % songs.size
        }

        playSong(songs[prevIndex])
        viewModel.setCurrentSongIndex(prevIndex)
    }


    // Hàm chơi nhạc sau khi nhảy bài hát
    private fun playSong(song: DataSongList) {
        viewModel.releaseMediaPlayer()
        binding.songName.text = song.songName
        Glide.with(this).load(song.image).into(binding.imgSong)
        rotateAnimator.pause()
        // Cập nhật trạng thái yêu thích
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
    }





    override fun onDestroy() {
        super.onDestroy()
        viewModel.releaseMediaPlayer()
    }
}
