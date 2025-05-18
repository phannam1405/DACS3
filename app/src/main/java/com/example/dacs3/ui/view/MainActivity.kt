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
import com.example.dacs3.ui.adapter.SingerListAdapter
import com.example.dacs3.ui.adapter.SongListAdapter
import com.example.dacs3.ui.adapter.ViewPageMusicAdapter
import com.example.dacs3.ui.viewmodel.MainViewModel
import com.example.dacs3.ui.viewmodel.PlaylistChildViewModel
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
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

        // Khởi tạo ViewModel cho MainActivity
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        // Khởi tạo ViewModel cho Fragment
        viewModelChild = ViewModelProvider(this).get(PlaylistChildViewModel::class.java)

        // Khởi tạo carousel banner
        setupImageCarousel()

        // Setup Navigation Drawer
        setupNavigationView()

        // Setup Bottom Navigation
        setupBottomNavigation()

        // Khởi tạo tìm kiếm
        setupSearch()

        // Khởi tạo Danh sach nhac theo the loai
        setupMusicTabGenre()

        // Quan sát dữ liệu
        setUpSongList()

        //setupSingerList
        setupSingerList()
    }

    private fun setupSingerList() {
        viewModel.singerImages.observe(this) { imageList ->
            val adapter = SingerListAdapter(imageList)
            adapter.onItemClick = { singerImage ->
                val intent = Intent(this, SingerActivity::class.java).apply {
                    putExtra("singer_image", singerImage)
                }
                startActivity(intent)
            }
            binding.rvSingerList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            binding.rvSingerList.adapter = adapter
        }
    }
    // Setup TabLayout
    private fun setupMusicTabGenre() {
        viewModel.categories.observe(this) { categories ->
            if (categories.isNotEmpty()) {
                val adapter = ViewPageMusicAdapter(supportFragmentManager, lifecycle, categories)
                binding.viewPagerGenres.adapter = adapter

                TabLayoutMediator(binding.tabMusicGenres, binding.viewPagerGenres) { tab, position ->
                    tab.text = categories[position]
                }.attach()

                binding.viewPagerGenres.isUserInputEnabled = false
            }
        }
    }






    // Khởi tạo carousel
    private fun setupImageCarousel() {
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











    // Hàm khởi tạo và xử lí với bottom menu
    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.itemFav -> {
                    startActivity(Intent(this, FavouriteActivity::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)    //Hàm chuyên cảnh hiệu ứng fade
                    true
                }
                R.id.itemHome -> true
                R.id.itemFriend -> {
                    startActivity(Intent(this, FriendActivity::class.java))
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

    // Hàm xử lí với Navigation Drawer
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

















    // Hàm khởi tạo và xử lí khi người dùng nhấn vào Item của Search
    private fun setupSearch() {
        searchAdapter = SearchAdapter { song, songList ->
            val intent = Intent(this, PlayerActivity::class.java).apply {
                // Thông tin bài hát hiện tại
                putExtra("image", song.image)
                putExtra("song_id", song.id)
                putExtra("audio", song.audio)
                putExtra("song_name", song.songName)
                putExtra("song", song)
                putExtra("song_list", ArrayList(songList))
                putExtra("source", "song_list")
            }
            startActivity(intent)
            exitSearchMode()
        }
        binding.rVSearch.adapter = searchAdapter
        binding.rVSearch.layoutManager = LinearLayoutManager(this)

        // Setup SearchView
        setupSearchView()
    }


    // Khởi tạo và xử lí danh sách bài hát
    private fun setUpSongList() {
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
                        putExtra("song_name", songs[position].songName)
                        putExtra("song", songs[position])
                        putExtra("song_list", ArrayList(songs))
                        putExtra("source", "song_list")
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

        binding.rvSongList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvSongList.setHasFixedSize(true)
    }










    private fun setupSearchView() {
        binding.customToolbar.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            // khi người dùng submit
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.takeIf { it.isNotEmpty() }?.let {
                    enterSearchMode()
                    viewModel.searchSongs(it)
                }
                return false
            }

            // Khi người dùng thay đổi văn bản tìm kiếm
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


    // Hàm xử lí khi người dùng nhấn vào nút tìm kiếm
    private fun enterSearchMode() {
        isSearchMode = true
        binding.rVSearch.visibility = View.VISIBLE
        binding.carousel.visibility = View.GONE

    }


    // Hàm xử lí khi người dùng nhấn vào nút thoát khỏi chế độ tìm kiếm
    private fun exitSearchMode() {
        isSearchMode = false
        binding.rVSearch.visibility = View.GONE
        binding.carousel.visibility = View.VISIBLE

    }







    // Hàm khi bạn nhấn nút Back khi đang ở Activity này
    @Deprecated("Deprecated, but still used for back compatibility")  // Thông báo hàm cũ
    @Suppress("DEPRECATION")   // Bỏ qua cảnh báo
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

    // Khởi tạo Dialog hỏi bạn muôn đăng xuất
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


    override fun onResume() {
        super.onResume()
        binding.rVSearch.visibility = View.GONE
        viewModelChild.loadPlaylistsDad()
        binding.bottomNavigation.selectedItemId = R.id.itemHome
        binding.customToolbar.searchView.setQuery("", false)
        binding.customToolbar.searchView.clearFocus()
    }
}
