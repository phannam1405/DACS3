package com.example.dacs3.ui.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dacs3.R
import com.example.dacs3.data.model.DataSongList
import com.example.dacs3.databinding.ActivitySingerBinding
import com.example.dacs3.ui.adapter.SingerSongAdapter
import com.example.dacs3.ui.viewmodel.MainViewModel

class SingerActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySingerBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: SingerSongAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySingerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup toolbar
        binding.toolbarInclude.txtTitle.text = "Ca sĩ"
        binding.toolbarInclude.imgBack.setOnClickListener {
            finish()
        }

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        val singerImage = intent.getStringExtra("singer_image") ?: ""

        // Initialize RecyclerView
        setupRecyclerView()

        // Load songs by singer image
        viewModel.getSongsBySingerImage(singerImage).observe(this) { songs ->
            adapter = SingerSongAdapter(songs) { song ->
                // Handle song item click
                openPlayerActivity(song)
            }
            binding.rvSongs.adapter = adapter
        }
    }

    private fun setupRecyclerView() {
        binding.rvSongs.layoutManager = LinearLayoutManager(this)
        binding.rvSongs.setHasFixedSize(true)
    }

    private fun openPlayerActivity(song: DataSongList) {
        // Lấy toàn bộ danh sách bài hát từ adapter
        val songList = ArrayList<DataSongList>().apply {
            for (i in 0 until adapter.itemCount) {
                adapter.getItem(i)?.let { add(it) }
            }
        }

        // Tạo intent và truyền dữ liệu
        Intent(this, PlayerActivity::class.java).apply {
            putExtra("image", song.image)
            putExtra("song_id", song.id)
            putExtra("audio", song.audio)
            putExtra("song_name", song.songName)
            putExtra("song", song)
            putExtra("song_list", songList)
            startActivity(this)
        }
    }
}