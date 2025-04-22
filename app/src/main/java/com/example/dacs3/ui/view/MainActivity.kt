package com.example.dacs3.ui.view


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dacs3.data.model.DataSongList
import com.example.dacs3.databinding.ActivityMainBinding
import com.example.dacs3.ui.adapter.SongListAdapter
import com.example.dacs3.ui.viewmodel.MainViewModel
import com.example.dacs3.R
import com.example.dacs3.ui.adapter.AdapterCarousel
import com.example.dacs3.ui.adapter.AdapterSearch
import com.example.dacs3.ui.viewmodel.PlaylistChildViewModel
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.carousel.CarouselSnapHelper


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var viewModelChild: PlaylistChildViewModel
    private lateinit var searchAdapter: AdapterSearch
    private var isSearchMode = false



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

//        binding.customToolbar.imgAvatar.setOnClickListener{
//            startActivity(Intent(this, ProfileActivity::class.java))
//        }


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

        // Khởi tạo SearchAdapter
        searchAdapter = AdapterSearch { song ->
            val intent = Intent(this, PlayerActivity::class.java).apply {
                putExtra("image", song.image)
                putExtra("song_id", song.id)
                putExtra("audio", song.audio)
                putExtra("song_name", song.song_name)
                putExtra("song", song) // Vẫn giữ lại nếu có chỗ dùng
            }
            startActivity(intent)
            exitSearchMode()
        }
        binding.rVSearch.adapter = searchAdapter
        binding.rVSearch.layoutManager = LinearLayoutManager(this)

        // Xử lý sự kiện search
        setupSearchView()

        // Quan sát kết quả tìm kiếm
        viewModel.searchResults.observe(this) { results ->
            Log.d("MainActivity", "Search results received: ${results.size}")
            if (isSearchMode) {
                searchAdapter.updateData(results)
            }
        }



        // Quan sát dữ liệu từ ViewModel
        viewModel.songs.observe(this) { songs ->
            var adapter = SongListAdapter(songs)
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



                override fun onAddPlaylist(song: DataSongList) {
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

    private fun setupSearchView() {
        val searchView = binding.customToolbar.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    if (it.isNotEmpty()) {
                        enterSearchMode()
                        viewModel.searchSongs(it)
                    }
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    if (it.isNotEmpty()) {
                        enterSearchMode()
                        viewModel.searchSongs(it)
                    } else {
                        exitSearchMode()
                    }
                }
                return false
            }
        })
    }

    private fun enterSearchMode() {
        isSearchMode = true
        binding.rVSearch.visibility = View.VISIBLE
        binding.carousel.visibility = View.GONE
        binding.songListSection.visibility = View.GONE
    }

    private fun exitSearchMode() {
        isSearchMode = false
        binding.rVSearch.visibility = View.GONE
        binding.carousel.visibility = View.VISIBLE
        binding.songListSection.visibility = View.VISIBLE
    }


    //  Tự động cập nhật danh sách khi quay lại từ activity khác
    override fun onResume() {
        super.onResume()
        binding.rVSearch.visibility = View.GONE
        viewModelChild.loadPlaylistsDad()
        binding.bottomNavigation.selectedItemId = R.id.itemHome
        binding.customToolbar.searchView.setQuery("", false) // Xóa text ở tìm kiếm
        binding.customToolbar.searchView.clearFocus() // Ẩn bàn phím
    }




}
