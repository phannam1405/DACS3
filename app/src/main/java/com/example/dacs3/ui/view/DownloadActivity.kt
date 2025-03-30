package com.example.dacs3.ui.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.dacs3.data.database.MusicDatabase
import com.example.dacs3.R
import com.example.dacs3.data.model.Music
import com.example.dacs3.data.repository.MusicRepository
import com.example.dacs3.databinding.ActivityDownloadBinding
import com.example.dacs3.ui.adapter.DownloadAdapter
import com.example.dacs3.ui.viewmodel.DownloadViewModel

class DownloadActivity : AppCompatActivity(), DownloadAdapter.OnItemClickListener { // Sửa tên đúng chuẩn
    private lateinit var binding: ActivityDownloadBinding
    private lateinit var database: MusicDatabase
    lateinit var res: MusicRepository
    lateinit var rvDown: ListView
    private val viewModel: DownloadViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDownloadBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        database = MusicDatabase.getInstance(this)

        res = MusicRepository(application)
        var data: List<Music> = res.getAll() as List<Music>
        val musicArrayList: ArrayList<Music> = ArrayList()

        for (i in 0 until data.size) {
            val songName: String = data[i].songName
            val coverImage: String = data[i].coverImage
            val localAudioPath: String = data[i].localAudioPath
            val singerImage: String = data[i].singerImage
            val singer_name: String = data[i].singer_name
            val cate: String = data[i].cate
            musicArrayList.add(Music(songName, coverImage, localAudioPath, singerImage, cate, singer_name))
        }
        rvDown = findViewById<ListView>(R.id.lvDown)
        rvDown.adapter = DownloadAdapter(this, musicArrayList, this)

    }

    // Xử lý khi click vào item
    override fun ngheNhacOffline(position: Int) {
        val songList = res.getAll() ?: emptyList()
        val song = songList.getOrNull(position)
        song?.let {
            if (!it.localAudioPath.isNullOrEmpty()) {
                val intent = Intent(this@DownloadActivity, PlayerActivity::class.java)
                intent.putExtra("image", it.coverImage)
                intent.putExtra("audio", it.localAudioPath)
                Log.d("DownloadActivity", "localAudioPath: ${it.localAudioPath}")
                intent.putExtra("song_name", it.songName)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Bài hát không hợp lệ!", Toast.LENGTH_SHORT).show()
            }
        } ?: Toast.makeText(this, "Vị trí không hợp lệ!", Toast.LENGTH_SHORT).show()
    }


}
