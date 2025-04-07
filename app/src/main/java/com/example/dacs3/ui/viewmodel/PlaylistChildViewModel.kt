package com.example.dacs3.ui.viewmodel

import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.dacs3.data.model.OutdataPlaylistDad
import com.example.dacs3.data.model.OutdataSongList
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class PlaylistChildViewModel(application: Application) : AndroidViewModel(application) {

    private val _playlists = MutableLiveData<List<OutdataSongList>>()
    private val _playlistsDad = MutableLiveData<List<OutdataPlaylistDad>>()

    val playlist: LiveData<List<OutdataSongList>> get() = _playlists


    private val dbrefPlaylist = FirebaseDatabase.getInstance(
        "https://dacs3-7408e-default-rtdb.asia-southeast1.firebasedatabase.app"
    ).getReference("Playlist")
    private val dbrefSongs = FirebaseDatabase.getInstance(
        "https://dacs3-7408e-default-rtdb.asia-southeast1.firebasedatabase.app"
    ).getReference("Song")

    private var playlistId: String? = null

    // Set playlistId từ Activity
    fun setPlaylistId(playlistId: String) {
        this.playlistId = playlistId
        layNhacTrongPlaylist(playlistId)
    }



    // Hàm lấy nhạc trong playlist
    fun layNhacTrongPlaylist(playlistId: String) {
        dbrefPlaylist.child(playlistId).child("songs").get().addOnSuccessListener { snapshot ->
            val songIds = snapshot.children.filter { it.value == true }  // Chọn chỉ các bài hát có giá trị "true"
                .mapNotNull { it.key }  // Lấy songId

            val songList = mutableListOf<OutdataSongList>()
            val total = songIds.size
            var loaded = 0

            if (songIds.isEmpty()) {
                _playlists.value = songList
            }

            for (songId in songIds) {
                dbrefSongs.child(songId).get().addOnSuccessListener { songSnap ->
                    val song = songSnap.getValue(OutdataSongList::class.java)
                    song?.let {
                        it.id = songId // gắn id vào key_node
                        songList.add(it)
                    }

                    loaded++
                    if (loaded == total) {
                        _playlists.value = songList
                    }
                }
            }
        }
    }




    // Hàm thêm bài nhạc vào playlist
    fun addSongToPlaylist(playlistId: String, musicId: String) {
        val playlistRef = dbrefPlaylist.child(playlistId).child("songs")

        // Thêm bài nhạc vào playlist với key là musicId
        playlistRef.child(musicId).setValue(true)
    }
    fun loadPlaylistsDad() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        dbrefPlaylist.get().addOnSuccessListener { snapshot ->
            val list = mutableListOf<OutdataPlaylistDad>()
            for (child in snapshot.children) {
                val playlist = child.getValue(OutdataPlaylistDad::class.java)
                val owner = child.child("owner").getValue(String::class.java)

                if (playlist != null && owner == userId) {
                    playlist.id = child.key  // Gán ID = key trong Firebase
                    list.add(playlist)
                }
            }
            _playlistsDad.value = list
        }
    }



    fun showAddSongDialog(context: Context, musicId: String) {
        val playlists = _playlistsDad.value ?: return
        val checkedItems = BooleanArray(playlists.size) { false }
        val items = playlists.map { it.title ?: "Unknown Playlist" }.toTypedArray()

        val dialog = AlertDialog.Builder(context)
            .setTitle("Select Playlists to Add Song")
            .setMultiChoiceItems(items, checkedItems) { _, which, isChecked ->
                checkedItems[which] = isChecked
            }
            .setPositiveButton("Add") { _, _ ->
                for (i in checkedItems.indices) {
                    if (checkedItems[i]) {
                        val playlistId = playlists[i].id ?: continue
                        addSongToPlaylist(playlistId, musicId)
                    }
                }
                Toast.makeText(context, "Song added to selected playlists", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .create()

        // Đảm bảo rằng ListView đã được khởi tạo
        dialog.setOnShowListener {
            val listView = dialog.listView
            val params = listView.layoutParams
            params.height = 400  // Điều chỉnh chiều cao theo nhu cầu
            listView.layoutParams = params
        }

        dialog.show()
    }

    fun deleteSongFromPlaylist(id: String) {
        val currentPlaylistId = playlistId ?: return

        val songRef = dbrefPlaylist.child(currentPlaylistId).child("songs").child(id)

        songRef.removeValue()
        layNhacTrongPlaylist(currentPlaylistId)
    }






}


