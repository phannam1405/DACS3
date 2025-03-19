package com.example.dacs3

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import okhttp3.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dacs3.databinding.ActivityMainBinding
import com.google.firebase.database.*
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient


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
        supportActionBar?.hide()


        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

        // Cấu hình RecyclerView
        binding.rvSongList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvSongList.setHasFixedSize(true)
        // Khởi tạo danh sách bài hát
        sl = arrayListOf<Outdata_Song_List>()
        layThongTinKhoNhac()


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
                R.id.itemDownload -> {
                    startActivity(Intent(this, Download::class.java))
                    true
                }
                else -> false
            }
        }
    }

    //hàm tải nhạc
    fun downloadSong(song: Outdata_Song_List) {
        CoroutineScope(Dispatchers.IO).launch {
            val client = OkHttpClient()
            val downloadTasks = listOfNotNull(
                song.audio?.let { it to "${song.song_name?.replace(" ", "_") ?: "default_song"}.mp3" },
                song.image?.let { it to "cover_${song.song_name?.replace(" ", "_") ?: "default"}.jpg" },
                song.singer?.let { it to "singer_${song.song_name?.replace(" ", "_") ?: "default"}.jpg" }
            )

            val downloadedPaths = mutableMapOf<String, String>()
            var hasFailed = false

            // Duyệt danh sách URL cần tải và thực hiện tải từng file
            for ((url, fileName) in downloadTasks) {
                try {
                    val request = Request.Builder().url(url).build()
                    val response = client.newCall(request).execute()

                    if (response.isSuccessful) {
                        val file = File(getExternalFilesDir(null), fileName)
                        response.body?.byteStream()?.use { input ->
                            FileOutputStream(file).use { output -> input.copyTo(output) }
                        }
                        downloadedPaths[url] = file.absolutePath
                    } else {
                        hasFailed = true
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    hasFailed = true
                }
            }

            // Chuyển về Main Thread để cập nhật UI
            withContext(Dispatchers.Main) {
                if (hasFailed) {
                    Toast.makeText(this@MainActivity, "Tải một số file thất bại!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@MainActivity, "Tải thành công!", Toast.LENGTH_SHORT).show()
                }

                // Lưu vào database
                saveToDatabase(
                    song,
                    downloadedPaths[song.audio] ?: "",
                    downloadedPaths[song.image] ?: "",
                    downloadedPaths[song.singer] ?: ""
                )
            }
        }
    }



    //hàm lưu nhạc
    fun saveToDatabase(song: Outdata_Song_List, filePath: String, coverPath: String, singerPath: String) {
        val database = MusicDatabase.getInstance(this)
        val musicDao = database.myDao()

        // Log đường dẫn file trước khi lưu
        Log.d("DatabaseCheck", "Song: ${song.song_name}")
        Log.d("DatabaseCheck", "Cover Image Path: $coverPath")
        Log.d("DatabaseCheck", "Singer Image Path: $singerPath")
        Log.d("DatabaseCheck", "Audio File Path: $filePath")
        val music = Music(
            songName = song.song_name ?: "Unknown",
            coverImage = coverPath,
            localAudioPath = filePath,
            singerImage = singerPath,
            cate = song.cate ?: "Unknown",
            singer_name = song.singer_name!!
        )

        musicDao.insert(music)
        Toast.makeText(this, "Lưu vào database thành công!", Toast.LENGTH_SHORT).show()

    }




    // Lấy dữ liệu bài hát từ Firebase
    private fun layThongTinKhoNhac() {
        Log.d("Firebase", "Hàm layThongTinKhoNhac() đã được gọi")
        dbref = FirebaseDatabase.getInstance("https://dacs3-7408e-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("Song")

        dbref.addValueEventListener(object : ValueEventListener {
            @SuppressLint("ClickableViewAccessibility")
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("Firebase", "Đã nhận dữ liệu từ Firebase")

                sl.clear()
                if (snapshot.exists()) {
                    for (musicSnapshot in snapshot.children) {
                        val song_name = musicSnapshot.child("song_name").getValue(String::class.java)
                        val image = musicSnapshot.child("image").getValue(String::class.java)
                        val audio = musicSnapshot.child("audio").getValue(String::class.java)
                        val singer = musicSnapshot.child("singer").getValue(String::class.java)
                        val singer_name = musicSnapshot.child("singer_name").getValue(String::class.java)
                        val cate = musicSnapshot.child("cate").getValue(String::class.java)
                        if (song_name != null && image != null) {
                            val song = Outdata_Song_List(song_name, image, audio, singer,cate,singer_name)
                            sl.add(song)
                        }
                    }

                    // Cập nhật RecyclerView

                    val adapter = Adapter_Song_List(sl)
                    binding.rvSongList.adapter = adapter


                    val rootLayout = findViewById<View>(R.id.rootLayout)
                    rootLayout.setOnClickListener {
                        adapter.clearSelectedPosition() // Đảm bảo gọi đúng adapter
                        Toast.makeText(this@MainActivity, "s", Toast.LENGTH_SHORT).show()
                    }


                    // Xử lý sự kiện khi nhấn vào bài hát
                    adapter.setOnItemClickListenner(object : Adapter_Song_List.onItemClickListenner {
                        override fun onItemClick(position: Int) {
                            val intent = Intent(this@MainActivity, PlayerActivity::class.java)
                            intent.putExtra("singer", sl[position].singer)
                            intent.putExtra("audio", sl[position].audio)
                            intent.putExtra("song_name", sl[position].song_name)
                            startActivity(intent)
                        }


                        // xử lý khi download nhạc
                        override fun onDownloadClicked(song: Outdata_Song_List) {
                            downloadSong(song)
                            Toast.makeText(this@MainActivity, "Tải về ${song.song_name}", Toast.LENGTH_SHORT).show()
                        }
                    })
                } else {
                    Log.d("Firebase", "Không có dữ liệu trong Firebase")
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
