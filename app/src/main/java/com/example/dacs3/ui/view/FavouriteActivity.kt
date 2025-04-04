package com.example.dacs3.ui.view

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.dacs3.databinding.ActivityFavouriteBinding
import com.example.dacs3.ui.adapter.FavouriteAdapter
import androidx.activity.viewModels
import com.example.dacs3.ui.viewmodel.FavouriteViewModel


class FavouriteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavouriteBinding
    private lateinit var adapter: FavouriteAdapter
    private val viewModel: FavouriteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFavouriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        adapter = FavouriteAdapter(this, emptyList())
        binding.lvMusicFav.adapter = adapter

        // Lắng nghe dữ liệu từ ViewModel
        viewModel.favourites.observe(this) { list ->
            adapter = FavouriteAdapter(this, list)
            binding.lvMusicFav.adapter = adapter
        }

        // Gọi hàm để lấy dữ liệu
        viewModel.loadFavourites()
    }
}
