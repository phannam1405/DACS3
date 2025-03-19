package com.example.dacs3

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dacs3.databinding.ActivityDownloadBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Download : AppCompatActivity() {
    private lateinit var adapterDown: Dowload_Adapter
    private lateinit var binding: ActivityDownloadBinding
    private lateinit var database: MusicDatabase
    lateinit var res: MusicRepository
    lateinit var rvDown: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDownloadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        database = MusicDatabase.getInstance(this)

        // Lấy danh sách nhạc đã tải từ database
        res = MusicRepository(application)
        var data: List<Music> = res.getAll() as List<Music>
        val musicArrayList: ArrayList<Music> = ArrayList()

        for (i in 0 until data.size) {
            val songName: String = data.get(i).songName
            val coverImage: String = data.get(i).coverImage
            val localAudioPath: String = data.get(i).localAudioPath
            val singerImage: String = data.get(i).singerImage
            val singer_name: String = data.get(i).singer_name
            val cate: String = data.get(i).cate
            musicArrayList.add(Music(songName, coverImage, localAudioPath, singerImage, cate, singer_name))
        }

        rvDown = findViewById<ListView>(R.id.lvDown)


        rvDown.adapter = Dowload_Adapter(this, musicArrayList)
    }


}
