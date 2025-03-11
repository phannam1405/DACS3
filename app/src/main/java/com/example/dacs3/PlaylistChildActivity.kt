package com.example.dacs3

import android.os.Bundle
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.dacs3.databinding.ActivityPlaylistChildListBinding


class PlaylistChildActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlaylistChildListBinding
    lateinit var adapter_playlist_child: Adapter_Playlist_Child
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityPlaylistChildListBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)


        supportActionBar?.hide()
        // Khai bao danh sach yeu thich cua nguoi dung
        val list = mutableListOf<Outdata_playlist_child>()

        adapter_playlist_child = Adapter_Playlist_Child(this, list)
        binding.lvPlayListChild.adapter = adapter_playlist_child
    }
}