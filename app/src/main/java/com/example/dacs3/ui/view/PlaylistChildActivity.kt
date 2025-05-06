package com.example.dacs3.ui.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import com.example.dacs3.data.model.DataSongList
import com.example.dacs3.databinding.ActivityPlaylistChildListBinding
import com.example.dacs3.ui.adapter.PlaylistChildAdapter
import com.example.dacs3.ui.viewmodel.PlaylistChildViewModel

class PlaylistChildActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlaylistChildListBinding
    private lateinit var viewModel: PlaylistChildViewModel
    private lateinit var adapterPlaylistChild: PlaylistChildAdapter
    private var playlistId: String? = null
    private var title: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPlaylistChildListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
        getIntentData()
        setupViewModel()
        observePlaylist()
    }

    private fun initUI() {
        supportActionBar?.hide()
        setupToolbar()
    }

    private fun getIntentData() {
        playlistId = intent.getStringExtra("playlistId")
        title = intent.getStringExtra("playlistName")
        binding.toolbarInclude.txtTitle.text = title
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            SavedStateViewModelFactory(application, this)
        ).get(PlaylistChildViewModel::class.java)

        playlistId?.let { viewModel.setPlaylistId(it) }
    }

    private fun observePlaylist() {
        viewModel.playlist.observe(this, Observer { playlist ->
            playlist?.let {
                setupAdapter(it)
            }
        })
    }

    private fun setupToolbar() {
        binding.toolbarInclude.imgBack.setOnClickListener {
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    private fun setupAdapter(playlist: List<DataSongList>) {
        adapterPlaylistChild = PlaylistChildAdapter(this, playlist)
        binding.lvPlayListChild.adapter = adapterPlaylistChild
        adapterPlaylistChild.updateData(playlist)

        setupItemClick()
        setupDeleteClick()
    }

    private fun setupItemClick() {
        adapterPlaylistChild.setOnItemClickListener(object : PlaylistChildAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {
                if (adapterPlaylistChild.count == 0) return
                val selectedSong = adapterPlaylistChild.getItem(position)
                selectedSong?.let {
                    openPlayer(it)
                } ?: Toast.makeText(this@PlaylistChildActivity, "oh no", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun openPlayer(song: DataSongList) {
        val intent = Intent(this, PlayerActivity::class.java).apply {
            putExtra("image", song.image)
            putExtra("song_id", song.id)
            putExtra("audio", song.audio)
            putExtra("song_name", song.songName)
            putExtra("playlist_id", playlistId)
            putExtra("source", "playlist")
        }
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    private fun setupDeleteClick() {
        adapterPlaylistChild.setOnDeleteClickListener(object : PlaylistChildAdapter.OnDeleteClickListener {
            override fun onDeleteClick(position: Int) {
                val selectedSong = adapterPlaylistChild.getItem(position)
                selectedSong?.id?.let { songId ->
                    viewModel.deleteSongFromPlaylist(songId)
                    Toast.makeText(this@PlaylistChildActivity, "Đã xoá bài hát khỏi playlist", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}
