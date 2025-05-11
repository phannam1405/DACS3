package com.example.dacs3.ui.view

import android.content.Intent
import android.os.Bundle
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.dacs3.data.database.MusicDatabase
import com.example.dacs3.R
import com.example.dacs3.data.model.Music
import com.example.dacs3.data.repository.MusicRepository
import com.example.dacs3.databinding.ActivityDownloadBinding
import com.example.dacs3.ui.adapter.DownloadAdapter

class DownloadActivity : AppCompatActivity(), DownloadAdapter.OnItemClickListener {

    private lateinit var binding: ActivityDownloadBinding
    private lateinit var database: MusicDatabase
    private lateinit var musicRepository: MusicRepository
    private lateinit var rvDown: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDownloadBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        // Khởi tạo và thiết lập Toolbar
        setupToolbar()
        // Khởi tạo và thiết lập Database và Repository
        initializeDatabase()
        // Khởi tạo và thiết lập ListView
        setupListView()
        // Lấy dữ liệu và thiết lập Adapter cho ListView
        loadData()
    }




    // Khởi tạo và thiết lập Toolbar
    private fun setupToolbar() {
        binding.toolbarInclude.txtTitle.text = "NHẠC TẢI XUỐNG"
        binding.toolbarInclude.imgBack.setOnClickListener { finish() }
    }




    // Khởi tạo và thiết lập Database và Repository
    private fun initializeDatabase() {
        database = MusicDatabase.getInstance(this)
        musicRepository = MusicRepository(application)
    }




    // Lấy dữ liệu và thiết lập Adapter cho ListView
    private fun loadData() {
        val data: List<Music> = musicRepository.getAll() as List<Music>
        val musicArrayList = ArrayList<Music>()

        for (i in 0 until data.size) {
            val song = data[i]
            musicArrayList.add(Music(
                song.songName,
                song.coverImage,
                song.localAudioPath,
                song.singerImage,
                song.cate,
                song.singer_name
            ))
        }
        setupListViewAdapter(musicArrayList)
    }




    // Thiết lập ListView
    private fun setupListView() {
        rvDown = findViewById(R.id.lvDown)
    }





    // Thiết lập Adapter cho ListView
    private fun setupListViewAdapter(musicArrayList: ArrayList<Music>) {
        rvDown.adapter = DownloadAdapter(this, musicArrayList, this)
    }






    // Xử lý khi click vào item
    override fun ngheNhacOffline(position: Int) {
        val songList = musicRepository.getAll() ?: emptyList()
        val song = songList.getOrNull(position)

        song?.let {
            if (!it.localAudioPath.isNullOrEmpty()) {
                val intent = Intent(this@DownloadActivity, PlayerActivity::class.java).apply {
                    putExtra("image", it.coverImage)
                    putExtra("audio", it.localAudioPath)
                    putExtra("song_name", it.songName)
                }
                startActivity(intent)
            } else {
                showToast("Bài hát không hợp lệ!")
            }
        } ?: showToast("Vị trí không hợp lệ!")
    }





    // Hiển thị Toast
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
