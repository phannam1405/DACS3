package com.example.dacs3

import android.os.Bundle
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.dacs3.databinding.ActivityFavouriteBinding

class FavouriteActivity : AppCompatActivity() {
    lateinit var adapter_fav: Adapter_Favourite
    private lateinit var binding: ActivityFavouriteBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityFavouriteBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        supportActionBar?.hide()
        // Khai bao danh sach yeu thich cua nguoi dung
        val list = mutableListOf<Outdata_Fav>()
        list.add(Outdata_Fav(R.drawable.singer_minh_gay, "Bạc Phận", "Minh Gay", "4:32"))

        adapter_fav = Adapter_Favourite(this, list)
        val listViewFav = findViewById<ListView>(R.id.lvMusicFav)
        binding.lvMusicFav.adapter = adapter_fav
    }
}