package com.example.dacs3.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.dacs3.data.database.MusicDatabase
import com.example.dacs3.data.model.Music
import com.example.dacs3.data.model.OutdataSongList
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import java.io.File
import java.io.FileOutputStream
import okhttp3.Request
import kotlinx.coroutines.withContext


class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val _songs = MutableLiveData<List<OutdataSongList>>()
    val songs: LiveData<List<OutdataSongList>> get() = _songs

    private val database = MusicDatabase.getInstance(application)
    private val musicDao = database.myDao()

    private val dbref: DatabaseReference = FirebaseDatabase.getInstance(
        "https://dacs3-7408e-default-rtdb.asia-southeast1.firebasedatabase.app"
    ).getReference("Song")

    init {
        layThongTinKhoNhac()
    }

    private fun layThongTinKhoNhac() {
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val songList = mutableListOf<OutdataSongList>()
                if (snapshot.exists()) {
                    for (musicSnapshot in snapshot.children) {
                        val song = musicSnapshot.getValue(OutdataSongList::class.java)
                        song?.let { songList.add(it) }
                    }
                }
                _songs.postValue(songList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Lỗi lấy dữ liệu: ${error.message}")
            }
        })
    }

    fun downloadSong(song: OutdataSongList, onComplete: (Boolean) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val client = OkHttpClient()
            val downloadedPaths = mutableMapOf<String, String>()
            var hasFailed = false

            val downloadTasks = listOfNotNull(
                song.audio?.let { it to "${song.song_name?.replace(" ", "_") ?: "default_song"}.mp3" },
                song.image?.let { it to "cover_${song.song_name?.replace(" ", "_") ?: "default"}.jpg" },
                song.singer?.let { it to "singer_${song.song_name?.replace(" ", "_") ?: "default"}.jpg" }
            )

            for ((url, fileName) in downloadTasks) {
                try {
                    val request = Request.Builder().url(url).build()
                    val response = client.newCall(request).execute()

                    if (response.isSuccessful) {
                        val file = File(getApplication<Application>().getExternalFilesDir(null), fileName)
                        response.body?.byteStream()?.use { input ->
                            FileOutputStream(file).use { output -> input.copyTo(output) }
                        }
                        downloadedPaths[url] = file.absolutePath
                    } else {
                        hasFailed = true
                    }
                } catch (e: Exception) {
                    hasFailed = true
                }
            }

            withContext(Dispatchers.Main) {
                saveToDatabase(song, downloadedPaths[song.audio] ?: "", downloadedPaths[song.image] ?: "", downloadedPaths[song.singer] ?: "")
                onComplete(!hasFailed)
            }
        }
    }

    private fun saveToDatabase(song: OutdataSongList, filePath: String, coverPath: String, singerPath: String) {
        val music = Music(
            songName = song.song_name ?: "Unknown",
            coverImage = coverPath,
            localAudioPath = filePath,
            singerImage = singerPath,
            cate = song.cate ?: "Unknown",
            singer_name = song.singer_name ?: "Unknown"
        )
        CoroutineScope(Dispatchers.IO).launch {
            musicDao.insert(music)
        }
    }
}
