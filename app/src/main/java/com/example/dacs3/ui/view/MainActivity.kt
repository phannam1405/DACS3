package com.example.dacs3.ui.view


import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dacs3.data.model.OutdataSongList
import com.example.dacs3.databinding.ActivityMainBinding
import com.example.dacs3.ui.adapter.SongListAdapter
import com.example.dacs3.ui.viewmodel.MainViewModel
import com.example.dacs3.R



class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        binding.rvSongList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvSongList.setHasFixedSize(true)


        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.itemFav -> {
                    startActivity(Intent(this, FavouriteActivity::class.java))
                    true
                }
                R.id.itemHome -> {
                    true
                }
                R.id.itemDownload -> {
                    startActivity(Intent(this, DownloadActivity::class.java))
                    true
                }
                R.id.itemPlaylist -> {
                    startActivity(Intent(this, PlaylistDadActivity::class.java))
                    true
                }
                else -> false
            }
        }


        // Quan sát dữ liệu từ ViewModel
        viewModel.songs.observe(this) { songs ->
            val adapter = SongListAdapter(songs)
            binding.rvSongList.adapter = adapter

            adapter.setOnItemClickListenner(object : SongListAdapter.onItemClickListenner {
                override fun onItemClick(position: Int) {
                    val intent = Intent(this@MainActivity, PlayerActivity::class.java)
                    intent.putExtra("image", songs[position].image)
                    intent.putExtra("audio", songs[position].audio)
                    intent.putExtra("song_name", songs[position].song_name)
                    startActivity(intent)
                }

                override fun onDownloadClicked(song: OutdataSongList) {
                    viewModel.downloadSong(song) { success ->
                        val message = if (success) "Tải thành công!" else "Tải thất bại!"
                        Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }
    }
}
