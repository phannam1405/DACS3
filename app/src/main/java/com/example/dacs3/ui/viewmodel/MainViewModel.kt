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

    private val _searchResults = MutableLiveData<List<DataSongList>>()
    val searchResults: LiveData<List<DataSongList>> get() = _searchResults

    private val dbref: DatabaseReference = FirebaseDatabase.getInstance(
        "https://dacs3-7408e-default-rtdb.asia-southeast1.firebasedatabase.app"
    ).getReference("Song")

    init {
        layThongTinKhoNhac()
    }

    fun getSongsByCategory(category: String): LiveData<List<DataSongList>> {
        val filteredSongs = MutableLiveData<List<DataSongList>>()

        dbref.orderByChild("category").equalTo(category).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val songList = mutableListOf<DataSongList>()
                if (snapshot.exists()) {
                    for (musicSnapshot in snapshot.children) {
                        val song = musicSnapshot.getValue(DataSongList::class.java)
                        song?.let {
                            it.id = musicSnapshot.key
                            songList.add(it)
                        }
                    }
                }
                filteredSongs.postValue(songList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Lỗi lấy dữ liệu: ${error.message}")
            }
        })

        return filteredSongs
    }



    private fun layThongTinKhoNhac() {
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val songList = mutableListOf<DataSongList>()
                if (snapshot.exists()) {
                    for (musicSnapshot in snapshot.children) {
                        val song = musicSnapshot.getValue(DataSongList::class.java)
                        song?.let {
                            it.id = musicSnapshot.key
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


    fun searchSongs(query: String) {
        val queryLower = query.lowercase().trim()
        if (queryLower.isEmpty()) {
            _searchResults.postValue(emptyList())
            return
        }

        dbref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val results = mutableListOf<DataSongList>()
                for (musicSnapshot in snapshot.children) {
                    val song = musicSnapshot.getValue(DataSongList::class.java)?.apply {
                        id = musicSnapshot.key
                    }

                    val matches = song?.let {
                        it.songName?.lowercase()?.contains(queryLower) == true ||
                                it.singerName?.lowercase()?.contains(queryLower) == true
                    } ?: false

                    if (matches) {
                        song?.let { results.add(it) }
                    }
                }
                Log.d("SearchDebug", "Found ${results.size} results for query: $query")
                _searchResults.postValue(results)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Search error: ${error.message}")
            }
        })
    }
}
