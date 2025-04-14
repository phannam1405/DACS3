package com.example.dacs3.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.dacs3.data.model.DataSongList
import com.example.dacs3.data.repository.MusicRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val _songs = MutableLiveData<List<DataSongList>>()
    val songs: LiveData<List<DataSongList>> get() = _songs

    private val musicRepository = MusicRepository(application)

    private val dbref: DatabaseReference = FirebaseDatabase.getInstance(
        "https://dacs3-7408e-default-rtdb.asia-southeast1.firebasedatabase.app"
    ).getReference("Song")

    init {
        layThongTinKhoNhac()
    }


    private fun layThongTinKhoNhac() {
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val songList = mutableListOf<DataSongList>()
                if (snapshot.exists()) {
                    for (musicSnapshot in snapshot.children) {
                        val song = musicSnapshot.getValue(DataSongList::class.java)
                        song?.let {
                            it.id = musicSnapshot.key  // Gán key của Firebase vào thuộc tính id
                            songList.add(it)
                        }
                    }
                }
                _songs.postValue(songList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Lỗi lấy dữ liệu: ${error.message}")
            }
        })
    }
}
