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
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val playlistsQuery = dbref.orderByChild("owner").equalTo(userId)

        playlistsQuery.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val playLists = mutableListOf<OutdataPlaylistDad>()
                if (snapshot.exists()) {
                    for (musicSnapshot in snapshot.children) {
                        val playlist = musicSnapshot.getValue(OutdataPlaylistDad::class.java)
                        val id = musicSnapshot.key  // <- lấy key làm id
                        if (playlist != null && id != null) {
                            val playlistWithId = OutdataPlaylistDad(
                                image = playlist.image,
                                title = playlist.title,
                                id = id // <- gán id thủ công
                            )
                            playLists.add(playlistWithId)
                        }
                    }
                }
                _playlists.postValue(playLists)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Lỗi lấy dữ liệu: ${error.message}")
            }
        })
    }

    fun AddPlayListInFB(tieuDe: String) {


        dbref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var maxIndex = 0

                for (playlistSnapshot in snapshot.children) {
                    val key = playlistSnapshot.key
                    if (key != null && key.startsWith("playlist")) {
                        val numberPart = key.removePrefix("playlist").toIntOrNull()
                        if (numberPart != null && numberPart > maxIndex) {
                            maxIndex = numberPart
                        }
                    }
                }

                val newId = "playlist${maxIndex + 1}"
                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

                val playlist = OutdataPlaylistDad(
                    owner = userId,
                    title = tieuDe
                )

                // Ghi playlist và khởi tạo songs là rỗng
                val playlistMap = mapOf(
                    "owner" to userId,
                    "title" to tieuDe,
                    "songs" to emptyMap<String, Boolean>()  // <- Tạo songs rỗng
                )

                dbref.child(newId).setValue(playlistMap)
                    .addOnSuccessListener {
                        Log.d("Firebase", "Tạo playlist mới thành công với ID: $newId")
                    }
                    .addOnFailureListener {
                        Log.e("Firebase", "Lỗi khi tạo playlist: ${it.message}")
                    }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Lỗi truy cập Firebase: ${error.message}")
            }
        })
    }



}