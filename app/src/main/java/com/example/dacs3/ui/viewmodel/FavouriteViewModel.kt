package com.example.dacs3.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dacs3.data.model.DataSongList
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FavouriteViewModel : ViewModel() {

    // MutableLiveData để lưu danh sách bài hát yêu thích của người dùng
    private val _favourites = MutableLiveData<List<DataSongList>>()
    val favourites: LiveData<List<DataSongList>> = _favourites

    // Database references cho các node "Favourite" và "Song" trong Firebase Realtime Database
    private val dbrefFav = FirebaseDatabase.getInstance(
        "https://dacs3-7408e-default-rtdb.asia-southeast1.firebasedatabase.app"
    ).getReference("Favourite").child("owner")

    private val dbrefSongs = FirebaseDatabase.getInstance(
        "https://dacs3-7408e-default-rtdb.asia-southeast1.firebasedatabase.app"
    ).getReference("Song")

    // ID người dùng hiện tại từ Firebase Authentication
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    // Hàm tải danh sách yêu thích của người dùng từ Firebase
    fun loadFavourites() {
        val currentUserId = userId ?: return  // Nếu không có userId thì thoát

        // Lấy danh sách các bài hát yêu thích của người dùng từ Firebase
        dbrefFav.child(currentUserId).get().addOnSuccessListener { snapshot ->
            val favSongIds = mutableListOf<String>()
            // Lọc ra các bài hát mà người dùng đã thêm vào yêu thích
            for (child in snapshot.children) {
                val musicId = child.key
                val isFav = child.getValue(Boolean::class.java) == true
                if (isFav && musicId != null) {
                    favSongIds.add(musicId)  // Thêm vào danh sách nếu là bài hát yêu thích
                }
            }

            // Lấy thông tin chi tiết các bài hát từ node "Song"
            val songList = mutableListOf<DataSongList>()
            dbrefSongs.get().addOnSuccessListener { allSongsSnapshot ->
                // Lọc ra các bài hát từ danh sách tất cả bài hát có ID trong danh sách yêu thích
                for (songSnapshot in allSongsSnapshot.children) {
                    val songId = songSnapshot.key
                    if (songId in favSongIds) {
                        val song = songSnapshot.getValue(DataSongList::class.java)
                        song?.let {
                            it.id = songId // Gắn id từ key node vào đối tượng song
                            songList.add(it)
                        }
                    }
                }

                // Cập nhật giá trị cho LiveData để UI có thể quan sát và cập nhật
                _favourites.postValue(songList)
            }
        }
    }

    // Hàm thêm bài hát vào danh sách yêu thích của người dùng
    fun addSongToFavourite(songId: String) {
        val currentUserId = userId ?: return  // Nếu không có userId thì thoát

        // Lắng nghe sự kiện thay đổi trong danh sách yêu thích của người dùng
        dbrefFav.child(currentUserId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Nếu người dùng đã có danh sách yêu thích thì thêm bài hát vào
                    dbrefFav.child(currentUserId).child(songId).setValue(true)
                } else {
                    // Nếu danh sách yêu thích chưa có thì tạo mới
                    val newFavouriteMap = mapOf(
                        songId to true
                    )
                    dbrefFav.child(currentUserId).setValue(newFavouriteMap)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Xử lý lỗi nếu có
            }
        })
    }

    // Hàm kiểm tra bài hát có trong danh sách yêu thích không
    fun isSongFavourite(songId: String, onResult: (Boolean) -> Unit) {
        val currentUserId = userId ?: return  // Nếu không có userId thì thoát
        // Lấy trạng thái của bài hát trong danh sách yêu thích
        dbrefFav.child(currentUserId).child(songId).get()
            .addOnSuccessListener { snapshot ->
                val isFav = snapshot.getValue(Boolean::class.java) == true
                onResult(isFav)  // Gọi callback với kết quả
            }
    }

    // Hàm chuyển trạng thái yêu thích của bài hát (thêm hoặc xóa)
    fun toggleFavourite(songId: String) {
        val currentUserId = userId ?: return  // Nếu không có userId thì thoát
        val favRef = dbrefFav.child(currentUserId).child(songId)

        // Kiểm tra xem bài hát có trong danh sách yêu thích hay không
        favRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                removeSongFromFavourite(songId)  // Nếu có thì xóa
            } else {
                addSongToFavourite(songId)  // Nếu không thì thêm
            }
        }
    }

    // Hàm xóa bài hát khỏi danh sách yêu thích
    fun removeSongFromFavourite(songId: String) {
        val currentUserId = userId ?: return  // Nếu không có userId thì thoát
        dbrefFav.child(currentUserId).child(songId).removeValue()  // Xóa bài hát khỏi danh sách
        loadFavourites()  // Tải lại danh sách yêu thích
    }
}
