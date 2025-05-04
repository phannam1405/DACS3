package com.example.dacs3.ui.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dacs3.R
import com.example.dacs3.data.model.DataSongList
import com.example.dacs3.databinding.ActivityMainBinding
import com.example.dacs3.ui.adapter.CarouselAdapter
import com.example.dacs3.ui.adapter.SearchAdapter
import com.example.dacs3.ui.adapter.SongListAdapter
import com.example.dacs3.ui.adapter.ViewPageMusicAdapter
import com.example.dacs3.ui.viewmodel.MainViewModel
import com.example.dacs3.ui.viewmodel.PlaylistChildViewModel
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.carousel.CarouselSnapHelper
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    // Biến toàn cục
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var viewModelChild: PlaylistChildViewModel
    private lateinit var searchAdapter: SearchAdapter
    private var isSearchMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        // Khởi tạo ViewModel
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        viewModelChild = ViewModelProvider(this).get(PlaylistChildViewModel::class.java)

        // Khởi tạo layout bài hát
        setupSongList()

        // Khởi tạo carousel banner
        setupImageCarousel()

        // Setup Navigation Drawer
        setupNavigationView()

        // Setup Bottom Navigation
        setupBottomNavigation()

        // Khởi tạo tìm kiếm
        setupSearch()

        setupMusicTabGenre()

        // Quan sát dữ liệu
        observeData()
    }

    private fun setupMusicTabGenre() {
        val adapter = ViewPageMusicAdapter(supportFragmentManager, lifecycle)
        binding.viewPagerGenres.adapter = adapter
        TabLayoutMediator(binding.tabMusicGenres, binding.viewPagerGenres) { tab, position ->
            when (position) {
                0 -> tab.text = "Việt Nam"
                1 -> tab.text = "Trung Quốc"
                2 -> tab.text = "Nhật Bản"
                3 -> tab.text = "US-UK"
                4 -> tab.text = "Hàn Quốc"
            }
        }.attach()

        binding.viewPagerGenres.isUserInputEnabled = false
    }



    private fun setupSongList() {
        binding.rvSongList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvSongList.setHasFixedSize(true)
        CarouselSnapHelper().attachToRecyclerView(binding.rvSongList)
    }

    private fun setupImageCarousel() {
        binding.carousel.setHasFixedSize(true)
        binding.carousel.layoutManager = CarouselLayoutManager()

        val imageList = mutableListOf(
            R.drawable.carousel1,
            R.drawable.carousel2,
            R.drawable.carousel3,
            R.drawable.carousel4
        )

        val carouselAdapter = CarouselAdapter(imageList)
        binding.carousel.adapter = carouselAdapter
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.itemFav -> {
                    startActivity(Intent(this, FavouriteActivity::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    true
                }
                R.id.itemHome -> true
                R.id.itemFriend -> {
                    startActivity(Intent(this,FriendActivity::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    true
                }
                R.id.itemPlaylist -> {
                    startActivity(Intent(this, PlaylistDadActivity::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    true
                }
                else -> false
            }
        }
    }

    private fun setupSearch() {
        // Adapter cho kết quả tìm kiếm
        searchAdapter = SearchAdapter { song ->
            val intent = Intent(this, PlayerActivity::class.java).apply {
                putExtra("image", song.image)
                putExtra("song_id", song.id)
                putExtra("audio", song.audio)
                putExtra("song_name", song.song_name)
                putExtra("song", song)
            }
            startActivity(intent)
            exitSearchMode()
        }
        binding.rVSearch.adapter = searchAdapter
        binding.rVSearch.layoutManager = LinearLayoutManager(this)

        // Setup SearchView
        setupSearchView()
    }

    private fun observeData() {
        // Quan sát kết quả tìm kiếm
        viewModel.searchResults.observe(this) { results ->
            Log.d("MainActivity", "Search results received: ${results.size}")
            if (isSearchMode) {
                searchAdapter.updateData(results)
            }
        }

        // Quan sát danh sách bài hát
        viewModel.songs.observe(this) { songs ->
            val adapter = SongListAdapter(songs)
            binding.rvSongList.adapter = adapter

            adapter.setOnItemClickListenner(object : SongListAdapter.onItemClickListenner {
                override fun onItemClick(position: Int) {
                    val intent = Intent(this@MainActivity, PlayerActivity::class.java).apply {
                        putExtra("image", songs[position].image)
                        putExtra("song_id", songs[position].id)
                        putExtra("audio", songs[position].audio)
                        putExtra("song_name", songs[position].song_name)
                        putExtra("song", songs[position])
                    }
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
    }

    private fun setupNavigationView() {
        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_feedback -> {
                    Toast.makeText(this, "Clicked Feedback", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_settings -> {
                    Toast.makeText(this, "Clicked setting", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_info -> {
                    Toast.makeText(this, "Clicked Info", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    true
                }
                R.id.nav_logout -> {
                    showLogoutDialog()
                    true
                }
                else -> false
            }.also {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            }
        }
    }

    private fun setupSearchView() {
        val searchView = binding.customToolbar.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.takeIf { it.isNotEmpty() }?.let {
                    enterSearchMode()
                    viewModel.searchSongs(it)
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

    private fun showLogoutDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.custom_dialog_logout, null)
        builder.setView(dialogView)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val btnYes = dialogView.findViewById<Button>(R.id.btnYes)
        val btnNo = dialogView.findViewById<Button>(R.id.btnNo)

        btnYes.setOnClickListener {
            val intent = Intent(this, WelcomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        btnNo.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
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

    @Deprecated("Deprecated, but still used for back compatibility")
    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.custom_dialog_logout, null)
        builder.setView(dialogView)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val mes = dialogView.findViewById<TextView>(R.id.dialogMessage)
        val btnYes = dialogView.findViewById<Button>(R.id.btnYes)
        val btnNo = dialogView.findViewById<Button>(R.id.btnNo)

        mes.text = "Do you want to exit?"

        btnYes.setOnClickListener {
            dialog.dismiss()
            super.onBackPressed()
        }

        btnNo.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

        override fun onResume() {
        super.onResume()
        binding.rVSearch.visibility = View.GONE
        viewModelChild.loadPlaylistsDad()
        binding.bottomNavigation.selectedItemId = R.id.itemHome
        binding.customToolbar.searchView.setQuery("", false)
        binding.customToolbar.searchView.clearFocus()
    }
}
