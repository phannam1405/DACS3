package com.example.dacs3.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.dacs3.data.database.MusicDatabase
import com.example.dacs3.data.model.OutdataPlaylistDad
import com.example.dacs3.data.model.OutdataSongList
import com.example.dacs3.data.repository.MusicRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PlayListDadViewModel(application: Application) : AndroidViewModel(application) {
    private val _playlists = MutableLiveData<List<OutdataPlaylistDad>>()
    val playlist: LiveData<List<OutdataPlaylistDad>> get() = _playlists



    private val dbref: DatabaseReference = FirebaseDatabase.getInstance(
        "https://dacs3-7408e-default-rtdb.asia-southeast1.firebasedatabase.app"
    ).getReference("Playlist")



    init {
        layThongTinPlaylist()
    }


    private fun layThongTinPlaylist() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return  // Lấy UID người dùng hiện tại

        // Truy vấn các playlist của người dùng dựa trên UID
        val playlistsQuery = dbref.orderByChild("owner").equalTo(userId)

        playlistsQuery.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val playLists = mutableListOf<OutdataPlaylistDad>()
                if (snapshot.exists()) {
                    for (musicSnapshot in snapshot.children) {
                        val playlist = musicSnapshot.getValue(OutdataPlaylistDad::class.java)
                        playlist?.let { playLists.add(it) }
                    }
                }
                _playlists.postValue(playLists)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Lỗi lấy dữ liệu: ${error.message}")
            }
        })
    }
}