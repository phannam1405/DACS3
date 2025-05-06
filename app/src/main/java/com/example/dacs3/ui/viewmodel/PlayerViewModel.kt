package com.example.dacs3.ui.viewmodel

import android.app.Application
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.dacs3.data.model.Music
import com.example.dacs3.data.model.DataSongList
import com.example.dacs3.data.repository.MusicRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class PlayerViewModel(application: Application) : AndroidViewModel(application) {
    private var mediaPlayer: MediaPlayer? = null

    private val _isPlaying = MutableLiveData<Boolean>()
    val isPlaying: LiveData<Boolean> = _isPlaying

    private val _duration = MutableLiveData<Int>()
    val duration: LiveData<Int> = _duration

    private val _currentPosition = MutableLiveData<Int>()
    val currentPosition: LiveData<Int> = _currentPosition

    private val _mediaPlayerPrepared = MutableLiveData<Int>()

    private val musicRepository = MusicRepository(application)

    private var currentSongIndex = 0
    private var songsList: List<DataSongList> = emptyList()

    fun setSongsList(songs: List<DataSongList>) {
        songsList = songs
    }
    fun getCurrentSongIndex(): Int = currentSongIndex
    fun setCurrentSongIndex(index: Int) {
        currentSongIndex = index
    }



    fun prepareMediaPlayer(audio: String?) {
        releaseMediaPlayer()
        mediaPlayer = MediaPlayer()

        try {
            if (!audio.isNullOrEmpty()) {
                if (audio.startsWith("http")) {
                    mediaPlayer?.setDataSource(audio) // Phát online
                } else {
                    val context = getApplication<Application>().applicationContext
                    mediaPlayer?.setDataSource(context, Uri.parse(audio)) // Phát offline
                }

                mediaPlayer?.prepareAsync()
                mediaPlayer?.setOnPreparedListener {
                    _duration.postValue(it.duration)
                    _isPlaying.postValue(false)
                    _mediaPlayerPrepared.postValue(it.audioSessionId)  // Gửi audioSessionId
                }
            }
        } catch (e: IOException) {
            Log.e("PlayerViewModel", "Lỗi khi chuẩn bị MediaPlayer", e)
        }
    }


    fun play() {
        mediaPlayer?.let {
            if (!it.isPlaying) {
                it.start()
                _isPlaying.postValue(true)
                updateCurrentPosition()
            }
        }
    }


    fun pause() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                _isPlaying.postValue(false)
            }
        }
    }


    fun seekTo(position: Int) {
        mediaPlayer?.seekTo(position)
    }

    private fun updateCurrentPosition() {
        Thread {
            while (mediaPlayer?.isPlaying == true) {
                _currentPosition.postValue(mediaPlayer?.currentPosition ?: 0)
                Thread.sleep(1000)
            }
        }.start()
    }

    fun releaseMediaPlayer() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    fun downloadSong(song: DataSongList, onComplete: (Boolean) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val client = OkHttpClient()
            val downloadedPaths = mutableMapOf<String, String>()
            var hasFailed = false

            val downloadTasks = listOfNotNull(
                song.audio?.let { it to "${song.songName?.replace(" ", "_") ?: "default_song"}.mp3" },
                song.image?.let { it to "cover_${song.songName?.replace(" ", "_") ?: "default"}.jpg" },
                song.singerImage?.let { it to "singer_${song.songName?.replace(" ", "_") ?: "default"}.jpg" }
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
                saveToDatabase(song, downloadedPaths[song.audio] ?: "", downloadedPaths[song.image] ?: "", downloadedPaths[song.singerImage] ?: "")
                onComplete(!hasFailed)
            }
        }
    }

    fun getSongsList(): List<DataSongList> {
        return songsList
    }

    private fun saveToDatabase(song: DataSongList, filePath: String, coverPath: String, singerPath: String) {
        val music = Music(
            songName = song.songName ?: "Unknown",
            coverImage = coverPath,
            localAudioPath = filePath,
            singerImage = singerPath,
            cate = song.category ?: "Unknown",
            singer_name = song.singerName ?: "Unknown"
        )
        CoroutineScope(Dispatchers.IO).launch {
            musicRepository.insertMusic(music)
        }
    }
}
