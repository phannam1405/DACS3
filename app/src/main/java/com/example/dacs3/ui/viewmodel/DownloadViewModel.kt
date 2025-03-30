package com.example.dacs3.ui.viewmodel

import android.app.Application
import android.media.MediaMetadataRetriever
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.example.dacs3.data.model.Music
import com.example.dacs3.data.repository.MusicRepository

class DownloadViewModel(application: Application) : AndroidViewModel(application) {

    private val musicRepository = MusicRepository(application)



    // Xóa nhạc khỏi database
    fun deleteSong(song: Music, position: Int, list: MutableList<Music>, callback: (Boolean) -> Unit) {
        val deletedRows = musicRepository.deleteMusic(song)
        Log.d("DeleteSong", "Số dòng bị xóa: $deletedRows")
        if (deletedRows > 0) {
            list.removeAt(position)
            callback(true)
        } else {
            callback(false)
        }
    }

    // Lấy thời gian của audio để phục vụ cho seekbar
    fun getAudioDuration(audioPath: String?): String {
        if (audioPath.isNullOrEmpty()) return "00:00"
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(audioPath)
            val durationMs = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 0L
            retriever.release()
            val minutes = (durationMs / 1000) / 60
            val seconds = (durationMs / 1000) % 60
            String.format("%02d:%02d", minutes, seconds)
        } catch (e: Exception) {
            e.printStackTrace()
            "00:00"
        }
    }
}
