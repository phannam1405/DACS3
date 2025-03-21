package com.example.dacs3

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.dacs3.databinding.ActivityPlaylistDadBinding


class PlaylistDadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlaylistDadBinding
    lateinit var adapter_playlist_dad: Adapter_Playlist_Dad

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityPlaylistDadBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        supportActionBar?.hide()
        // Khai bao danh sach yeu thich cua nguoi dung
        val list = mutableListOf<Outdata_Playlist_Dad>()

        list.add(Outdata_Playlist_Dad(R.drawable.singer_minh_gay, "Minh Gay"))
        list.add(Outdata_Playlist_Dad(R.drawable.singer_minh_gay, "Minh Gay"))
        list.add(Outdata_Playlist_Dad(R.drawable.singer_minh_gay, "Minh Gay"))

        adapter_playlist_dad = Adapter_Playlist_Dad(this, list)
        binding.gvPlaylist.adapter = adapter_playlist_dad

    }
}