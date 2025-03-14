package com.example.dacs3

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dacs3.databinding.ActivityMainBinding
import com.google.firebase.database.*
import com.bumptech.glide.Glide


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var sl:ArrayList<Outdata_Song_List>
    private lateinit var dbref: DatabaseReference
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Cấu hình RecyclerView
        binding.rvSongList.layoutManager = LinearLayoutManager(this)
        binding.rvSongList.setHasFixedSize(true)
        // Khởi tạo danh sách bài hát
        sl = arrayListOf<Outdata_Song_List>()
        layThongTinKhoNhac()

        Glide.with(this).load("https://res.cloudinary.com/drqbcsyqj/image/upload/v1741861229/HongNhan_bqbzj7.jpg").into(binding.imageView2)

        // Drawer Navigation
        toggle = ActionBarDrawerToggle(this, binding.drawerLayout, R.string.open_nav, R.string.close_nav)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Xử lý sự kiện menu trong navigation drawer
        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_feedback -> Toast.makeText(this, "Feedback", Toast.LENGTH_SHORT).show()
                R.id.nav_info -> Toast.makeText(this, "About us", Toast.LENGTH_SHORT).show()
                R.id.nav_settings -> Toast.makeText(this, "Setting", Toast.LENGTH_SHORT).show()
                R.id.nav_logout -> Toast.makeText(this, "Log out", Toast.LENGTH_SHORT).show()
                R.id.nav_profile -> Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show()
            }
            true
        }

        // Xử lý sự kiện menu bottom navigation
        binding.bottomNavigation.selectedItemId = R.id.itemHome
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.itemFav -> {
                    startActivity(Intent(this, FavouriteActivity::class.java))
                    true
                }
                R.id.itemHome -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.itemPlaylist -> {
                    startActivity(Intent(this, PlayerActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    // Lấy dữ liệu bài hát từ Firebase
    private fun layThongTinKhoNhac() {
        dbref = FirebaseDatabase.getInstance().getReference("Song")
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                sl.clear()
                if (snapshot.exists()) {
                    for (songSnapshot in snapshot.children) {
                        val song_name = songSnapshot.child("song_name").getValue(String::class.java)
                        val image = songSnapshot.child("image").getValue(String::class.java)
                        val song = Outdata_Song_List(image, song_name)
                        sl.add(song)
                    }
                    val adapter = Adapter_Song_List(sl)
                    binding.rvSongList.adapter = adapter
                    Toast.makeText(this@MainActivity, "Có dữ liệu", Toast.LENGTH_SHORT).show()
                }else {
                   Toast.makeText(this@MainActivity, "Không có dữ liệu", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Lỗi lấy dữ liệu: ${error.message}")
            }
        })
    }


    // Xử lý sự kiện bấm nút Menu trong ActionBar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (toggle.onOptionsItemSelected(item)) true else super.onOptionsItemSelected(item)
    }
}
