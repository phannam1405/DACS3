package com.example.dacs3.ui.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

import androidx.lifecycle.Observer
import com.example.dacs3.data.model.DataSongList
import com.example.dacs3.databinding.ActivityPlaylistChildListBinding
import com.example.dacs3.ui.adapter.PlaylistChildAdapter
import com.example.dacs3.ui.viewmodel.PlaylistChildViewModel

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
        val playlistId = intent.getStringExtra("playlistId") ?: run {
            Toast.makeText(this, "Playlist ID không hợp lệ!", Toast.LENGTH_SHORT).show()
            finish()  // Kết thúc Activity nếu không có playlistId
            return
        }

        // Khởi tạo ViewModel với SavedStateViewModelFactory
        viewModel = ViewModelProvider(
            this,
            SavedStateViewModelFactory(application, this)
        ).get(PlaylistChildViewModel::class.java)


        // Ẩn Action Bar
        supportActionBar?.hide()

        // Khai báo danh sách bài hát
        val list = mutableListOf<DataSongList>()



        // Quan sát LiveData từ ViewModel
        viewModel.playlist.observe(this, Observer { playlist ->
            playlist?.let {
                // Khởi tạo Adapter
                adapter_playlist_child = PlaylistChildAdapter(this, playlist)
                binding.lvPlayListChild.adapter = adapter_playlist_child
                // Cập nhật danh sách bài hát vào Adapter
                adapter_playlist_child.updateData(it)
                adapter_playlist_child.setOnItemClickListener(object : PlaylistChildAdapter.onItemClickListener {
                    override fun onItemClick(position: Int) {
                    if (adapter_playlist_child.count == 0) return // chặn nếu danh sách rỗng

                    val selectedSong = adapter_playlist_child.getItem(position)
                    if (selectedSong != null) {
                        selectedSong.let {
                            val intent = Intent(this@PlaylistChildActivity, PlayerActivity::class.java)
                            intent.putExtra("image", it.image)
                            intent.putExtra("song_id", it.id)
                            intent.putExtra("audio", it.audio)
                            intent.putExtra("song_name", it.song_name)
                            startActivity(intent)
                        }
                    }else{
                        Toast.makeText(this@PlaylistChildActivity, "oh no", Toast.LENGTH_SHORT).show()

                    }



                    }
                })

                //chọn item để xoá
                adapter_playlist_child.setOnDeleteClickListener(object : PlaylistChildAdapter.OnDeleteClickListener {
                    override fun onDeleteClick(position: Int) {
                        val selectedSong = adapter_playlist_child.getItem(position)
                        selectedSong?.let {
                            it.id?.let { songId -> viewModel.deleteSongFromPlaylist(songId) }

                            Toast.makeText(this@PlaylistChildActivity, "Đã xoá bài hát khỏi playlist", Toast.LENGTH_SHORT).show()
                        }

                    }
                })



            }




        })


        viewModel.setPlaylistId(playlistId)

    }
}
