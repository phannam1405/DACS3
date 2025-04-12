package com.example.dacs3.ui.view


import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.helper.widget.Carousel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dacs3.data.model.OutdataSongList
import com.example.dacs3.databinding.ActivityMainBinding
import com.example.dacs3.ui.adapter.SongListAdapter
import com.example.dacs3.ui.viewmodel.MainViewModel
import com.example.dacs3.R
import com.example.dacs3.ui.adapter.AdapterCarousel
import com.example.dacs3.ui.viewmodel.PlaylistChildViewModel
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.carousel.CarouselSnapHelper


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var viewModelChild: PlaylistChildViewModel


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        viewModelChild = ViewModelProvider(this).get(PlaylistChildViewModel::class.java)
        viewModelChild.loadPlaylistsDad()

        binding.rvSongList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvSongList.setHasFixedSize(true)
        CarouselSnapHelper().attachToRecyclerView(binding.rvSongList)


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
                    intent.putExtra("song_id", songs[position].id)
                    intent.putExtra("audio", songs[position].audio)
                    intent.putExtra("song_name", songs[position].song_name)
                    intent.putExtra("song", songs[position])
                    startActivity(intent)
                }


                override fun onAddPlaylist(song: OutdataSongList) {
                    song.id?.let {
                        viewModelChild.loadPlaylistsDad()
                        viewModelChild.showAddSongDialog(this@MainActivity, it)
                    }
                }
            })
        }



        //Tạo carousel
        binding.carousel.setHasFixedSize(true)
        binding.carousel.layoutManager = CarouselLayoutManager()


        val imageList = mutableListOf<Int>()
        imageList.add(R.drawable.singer_minh_gay)
        imageList.add(R.drawable.error)
        imageList.add(R.drawable.singer_minh_gay)
        imageList.add(R.drawable.error)

        val adapterCarousel = AdapterCarousel(imageList)
        binding.carousel.adapter = adapterCarousel

    }

    //  Tự động cập nhật danh sách khi quay lại từ activity khác
    override fun onResume() {
        super.onResume()
        viewModelChild.loadPlaylistsDad()
        binding.bottomNavigation.selectedItemId = R.id.itemHome
    }




}
