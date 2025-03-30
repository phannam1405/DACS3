package com.example.dacs3.ui.media

import android.media.MediaPlayer
import android.net.Uri

object PlayerManager {
    private var mediaPlayer: MediaPlayer? = null
    var isPlaying: Boolean = false
        private set

    private var _duration: Int = 0
    val duration: Int
        get() = _duration

    private var _currentPosition: Int = 0
    val currentPosition: Int
        get() = _currentPosition

    private var isPrepared = false // Kiểm tra xem MediaPlayer đã chuẩn bị xong chưa

    fun prepareMediaPlayer(audio: String, uri: Uri?, onPrepared: (Int) -> Unit) {
        release() // Giải phóng MediaPlayer cũ trước khi tạo mới

        mediaPlayer = MediaPlayer().apply {
            try {
                if (audio.startsWith("http")) {
                    setDataSource(audio)
                } else if (uri != null) {
                    setDataSource(uri.toString())
                }
                prepareAsync()
                setOnPreparedListener {
                    _duration = duration // Cập nhật thời gian nhạc
                    isPrepared = true // Đánh dấu MediaPlayer đã sẵn sàng
                    onPrepared(_duration)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun play() {
        if (isPrepared) {
            mediaPlayer?.start()
            isPlaying = true
        }
    }

    fun pause() {
        mediaPlayer?.pause()
        isPlaying = false
    }

    fun seekTo(position: Int) {
        if (isPrepared) {
            mediaPlayer?.seekTo(position)
        }
    }

    fun updateProgress(): Int {
        if (isPrepared) {
            _currentPosition = mediaPlayer?.currentPosition ?: 0
        }
        return _currentPosition
    }

    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
        isPlaying = false
        _duration = 0
        _currentPosition = 0
        isPrepared = false
    }
}
