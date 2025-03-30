package com.example.dacs3.ui.view

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.dacs3.data.model.OutdataPlaylistDad
import com.example.dacs3.R
import com.example.dacs3.databinding.ActivityPlaylistDadBinding
import com.example.dacs3.ui.adapter.PlaylistDadAdapter


class PlaylistDadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlaylistDadBinding
    lateinit var adapter_playlist_dad: PlaylistDadAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityPlaylistDadBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        supportActionBar?.hide()
        // Khai bao danh sach yeu thich cua nguoi dung
        val list = mutableListOf<OutdataPlaylistDad>()

        list.add(OutdataPlaylistDad(R.drawable.singer_minh_gay, "Minh Gay"))
        list.add(OutdataPlaylistDad(R.drawable.singer_minh_gay, "Minh Gay"))
        list.add(OutdataPlaylistDad(R.drawable.singer_minh_gay, "Minh Gay"))

        adapter_playlist_dad = PlaylistDadAdapter(this, list)
        binding.gvPlaylist.adapter = adapter_playlist_dad

    }
}