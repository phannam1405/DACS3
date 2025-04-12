package com.example.dacs3.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dacs3.data.model.OutdataSongList
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FavouriteViewModel : ViewModel() {

    private val _favourites = MutableLiveData<List<OutdataSongList>>()
    val favourites: LiveData<List<OutdataSongList>> = _favourites


    private val dbrefFav = FirebaseDatabase.getInstance(
        "https://dacs3-7408e-default-rtdb.asia-southeast1.firebasedatabase.app"
    ).getReference("Favourite").child("owner")

    private val dbrefSongs = FirebaseDatabase.getInstance(
        "https://dacs3-7408e-default-rtdb.asia-southeast1.firebasedatabase.app"
    ).getReference("Song")

    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    // Hàm tải danh sách yêu thích của người dùng
    fun loadFavourites() {
        val currentUserId = userId ?: return

        dbrefFav.child(currentUserId).get().addOnSuccessListener { snapshot ->
            val favSongIds = mutableListOf<String>()
            for (child in snapshot.children) {
                val musicId = child.key
                val isFav = child.getValue(Boolean::class.java) == true
                if (isFav && musicId != null) {
                    favSongIds.add(musicId)
                }
            }

            val songList = mutableListOf<OutdataSongList>()
            dbrefSongs.get().addOnSuccessListener { allSongsSnapshot ->
                for (songSnapshot in allSongsSnapshot.children) {
                    val songId = songSnapshot.key
                    if (songId in favSongIds) {
                        val song = songSnapshot.getValue(OutdataSongList::class.java)
                        song?.let {
                            it.id = songId // GẮN id TỪ key node vào đây
                            songList.add(it)
                        }
                    }
                }

                _favourites.postValue(songList)
            }
        }
    }

    // Hàm thêm bài hát vào danh sách yêu thích
    fun addSongToFavourite(songId: String) {
        val currentUserId = userId ?: return

        dbrefFav.child(currentUserId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    dbrefFav.child(currentUserId).child(songId).setValue(true)
                } else {
                    val newFavouriteMap = mapOf(
                        songId to true
                    )
                    dbrefFav.child(currentUserId).setValue(newFavouriteMap)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }


    // Hàm kiểm tra bài hát có trong danh sách yêu thích không
    fun isSongFavourite(songId: String, onResult: (Boolean) -> Unit) {
        val currentUserId = userId ?: return
        dbrefFav.child(currentUserId).child(songId).get()
            .addOnSuccessListener { snapshot ->
                val isFav = snapshot.getValue(Boolean::class.java) == true
                onResult(isFav)
            }
    }


    fun toggleFavourite(songId: String) {
        val currentUserId = userId ?: return
        val favRef = dbrefFav.child(currentUserId).child(songId)

        favRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                removeSongFromFavourite(songId)
            } else {
                addSongToFavourite(songId)
            }
        }
    }



    // Hàm xóa bài hát khỏi danh sách yêu thích
    fun removeSongFromFavourite(songId: String) {
        val currentUserId = userId ?: return
        dbrefFav.child(currentUserId).child(songId).removeValue()
        loadFavourites()
    }
}

