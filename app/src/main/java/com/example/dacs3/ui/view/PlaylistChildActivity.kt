package com.example.dacs3.ui.view

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

import androidx.lifecycle.Observer
import com.example.dacs3.data.model.OutdataSongList
import com.example.dacs3.databinding.ActivityPlaylistChildListBinding
import com.example.dacs3.ui.adapter.PlaylistChildAdapter
import com.example.dacs3.ui.viewmodel.PlaylistChildViewModel

import androidx.activity.viewModels
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider

class PlaylistChildActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlaylistChildListBinding
    private lateinit var viewModel: PlaylistChildViewModel
    private lateinit var adapter_playlist_child: PlaylistChildAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaylistChildListBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        // Nhận playlistId từ Intent
        val playlistId = intent.getStringExtra("playlistId") ?: return

        // Khởi tạo ViewModel với SavedStateViewModelFactory
        viewModel = ViewModelProvider(
            this,
            SavedStateViewModelFactory(application, this)
        ).get(PlaylistChildViewModel::class.java)


        // Ẩn Action Bar
        supportActionBar?.hide()

        // Khai báo danh sách bài hát
        val list = mutableListOf<OutdataSongList>()

        // Khởi tạo Adapter
        adapter_playlist_child = PlaylistChildAdapter(this, list)
        binding.lvPlayListChild.adapter = adapter_playlist_child

        // Quan sát LiveData từ ViewModel
        viewModel.playlist.observe(this, Observer { songList ->
            songList?.let {
                // Cập nhật danh sách bài hát vào Adapter
                adapter_playlist_child.updateData(it)
            }
        })


        viewModel.setPlaylistId(playlistId)

    }
}
