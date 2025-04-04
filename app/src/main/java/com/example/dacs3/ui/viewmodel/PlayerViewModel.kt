package com.example.dacs3.ui.viewmodel

import android.app.Application
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.io.IOException

class PlayerViewModel(application: Application) : AndroidViewModel(application) {
    private var mediaPlayer: MediaPlayer? = null

    private val _isPlaying = MutableLiveData<Boolean>()
    val isPlaying: LiveData<Boolean> = _isPlaying

    private val _duration = MutableLiveData<Int>()
    val duration: LiveData<Int> = _duration

    private val _currentPosition = MutableLiveData<Int>()
    val currentPosition: LiveData<Int> = _currentPosition

    fun prepareMediaPlayer(audio: String?, uri: String?) {
        releaseMediaPlayer() // Giải phóng MediaPlayer
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
}
