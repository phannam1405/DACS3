package com.example.dacs3.ui.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.dacs3.databinding.ActivityPlaylistChildListBinding
import com.example.dacs3.ui.adapter.PlaylistChildAdapter
import com.example.dacs3.ui.viewmodel.PlaylistChildViewModel

import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider

class PlaylistChildActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlaylistChildListBinding
    private lateinit var viewModel: PlaylistChildViewModel
    private lateinit var adapterPlaylistChild: PlaylistChildAdapter

    // Hàm khởi tạo Activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaylistChildListBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        // Nhận playlistId từ Intent, nếu không có thì thông báo lỗi và kết thúc Activity
        val playlistId = intent.getStringExtra("playlistId") ?: run {
            Toast.makeText(this, "Playlist ID không hợp lệ!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Khởi tạo ViewModel với SavedStateViewModelFactory
        viewModel = ViewModelProvider(this, SavedStateViewModelFactory(application, this)).get(PlaylistChildViewModel::class.java)

        // Ẩn Action Bar
        supportActionBar?.hide()

        setupToolbar()

        var title = intent.getStringExtra("playlistName")
        binding.toolbarInclude.txtTitle.text = title

        // Quan sát LiveData từ ViewModel để nhận playlist dữ liệu
        viewModel.playlist.observe(this, Observer { playlist ->
            playlist?.let {
                // Khởi tạo Adapter và gán cho ListView
                adapterPlaylistChild = PlaylistChildAdapter(this, playlist)
                binding.lvPlayListChild.adapter = adapterPlaylistChild

                // Cập nhật dữ liệu mới vào Adapter
                adapterPlaylistChild.updateData(it)

                // Thiết lập sự kiện khi nhấn vào item trong danh sách
                adapterPlaylistChild.setOnItemClickListener(object : PlaylistChildAdapter.onItemClickListener {
                    override fun onItemClick(position: Int) {
                        // Kiểm tra xem danh sách có bài hát không, nếu không thì không làm gì
                        if (adapterPlaylistChild.count == 0) return

                        // Lấy bài hát được chọn và chuyển sang PlayerActivity
                        val selectedSong = adapterPlaylistChild.getItem(position)
                        if (selectedSong != null) {
                            selectedSong.let {
                                val intent = Intent(this@PlaylistChildActivity, PlayerActivity::class.java)
                                intent.putExtra("image", it.image)  // Truyền ảnh bài hát
                                intent.putExtra("song_id", it.id)   // Truyền ID bài hát
                                intent.putExtra("audio", it.audio)  // Truyền đường dẫn audio
                                intent.putExtra("song_name", it.song_name) // Truyền tên bài hát
                                startActivity(intent)
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                            }
                        } else {
                            Toast.makeText(this@PlaylistChildActivity, "oh no", Toast.LENGTH_SHORT).show()
                        }
                    }
                })

                // Thiết lập sự kiện khi nhấn vào nút xóa bài hát trong playlist
                adapterPlaylistChild.setOnDeleteClickListener(object : PlaylistChildAdapter.OnDeleteClickListener {
                    override fun onDeleteClick(position: Int) {
                        val selectedSong = adapterPlaylistChild.getItem(position)
                        selectedSong?.let {
                            it.id?.let { songId -> viewModel.deleteSongFromPlaylist(songId) }
                            Toast.makeText(this@PlaylistChildActivity, "Đã xoá bài hát khỏi playlist", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
            }
        })

        // Gọi hàm để đặt playlistId cho ViewModel
        viewModel.setPlaylistId(playlistId)
    }

    private fun setupToolbar() {
        binding.toolbarInclude.imgBack.setOnClickListener {

            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }
}
