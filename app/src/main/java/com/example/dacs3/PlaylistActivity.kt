package com.example.dacs3

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.dacs3.databinding.ActivityPlaylistBinding

class PlaylistActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlaylistBinding
    lateinit var adapter_playlist_dad: Adapter_Playlist_Dad

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityPlaylistBinding.inflate(layoutInflater)
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