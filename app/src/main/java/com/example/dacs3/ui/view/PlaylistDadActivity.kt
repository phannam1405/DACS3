package com.example.dacs3.ui.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.dacs3.data.model.OutdataPlaylistDad
import com.example.dacs3.R
import com.example.dacs3.data.model.OutdataSongList
import com.example.dacs3.databinding.ActivityPlaylistDadBinding
import com.example.dacs3.ui.adapter.PlaylistDadAdapter
import com.example.dacs3.ui.adapter.SongListAdapter
import com.example.dacs3.ui.viewmodel.MainViewModel
import com.example.dacs3.ui.viewmodel.PlayListDadViewModel


class PlaylistDadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlaylistDadBinding

    private lateinit var viewModel: PlayListDadViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityPlaylistDadBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        supportActionBar?.hide()

        viewModel = ViewModelProvider(this).get(PlayListDadViewModel::class.java)

        // Quan sát dữ liệu từ ViewModel
        viewModel.playlist.observe(this) { playlist ->
            val adapter = PlaylistDadAdapter(this, playlist)
            binding.gvPlaylist.adapter = adapter

            adapter.setOnItemClickListenner(object : PlaylistDadAdapter.onItemClickListenner {
                override fun onItemClick(position: Int) {
                    val intent = Intent(this@PlaylistDadActivity, PlaylistChildActivity::class.java)
                    intent.putExtra("image", playlist[position].image)
                    intent.putExtra("audio", playlist[position].title)

                    startActivity(intent)
                }


            })
        }

    }
}