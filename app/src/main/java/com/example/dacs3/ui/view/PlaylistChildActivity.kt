package com.example.dacs3.ui.view

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.dacs3.data.model.OutdataPlaylistChild
import com.example.dacs3.databinding.ActivityPlaylistChildListBinding
import com.example.dacs3.ui.adapter.PlaylistChildAdapter


class PlaylistChildActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlaylistChildListBinding
    lateinit var adapter_playlist_child: PlaylistChildAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityPlaylistChildListBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)


        supportActionBar?.hide()
        // Khai bao danh sach yeu thich cua nguoi dung
        val list = mutableListOf<OutdataPlaylistChild>()

        adapter_playlist_child = PlaylistChildAdapter(this, list)
        binding.lvPlayListChild.adapter = adapter_playlist_child
    }
}