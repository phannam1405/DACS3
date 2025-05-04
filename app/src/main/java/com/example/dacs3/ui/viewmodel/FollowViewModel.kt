package com.example.dacs3.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dacs3.data.model.DataUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FollowViewModel : ViewModel() {

    private val _followedUsers = MutableLiveData<List<DataUser>>()
    val followedUsers: LiveData<List<DataUser>> = _followedUsers

    private val database = FirebaseDatabase.getInstance(
        "https://dacs3-7408e-default-rtdb.asia-southeast1.firebasedatabase.app"
    )

    private val dbrefFollow = database.getReference("Follow")
    private val dbrefUsers = database.getReference("User") // Nếu bạn lưu người dùng trong "User"

    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    // Hàm tải danh sách người dùng đã follow
    fun loadFollowedUsers() {
        val currentUserId = userId ?: return

        dbrefFollow.child(currentUserId).get().addOnSuccessListener { snapshot ->
            val followedUserIds = mutableListOf<String>()
            for (child in snapshot.children) {
                val isFollowing = child.getValue(Boolean::class.java) == true
                val userFollowedId = child.key
                if (isFollowing && userFollowedId != null) {
                    followedUserIds.add(userFollowedId)
                }
            }

            val userList = mutableListOf<DataUser>()
            dbrefUsers.get().addOnSuccessListener { allUsersSnapshot ->
                for (userSnapshot in allUsersSnapshot.children) {
                    val uid = userSnapshot.key
                    if (uid in followedUserIds) {
                        val user = userSnapshot.getValue(DataUser::class.java)
                        user?.let {
                            it.id = uid  // ⚡⚡⚡ Gán id bằng key từ firebase
                            userList.add(it)
                        }
                    }
                }

                _followedUsers.postValue(userList)
            }
        }
    }

    // Hàm follow người dùng
    fun followUser(userIdToFollow: String) {
        val currentUserId = userId ?: return

        dbrefFollow.child(currentUserId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    dbrefFollow.child(currentUserId).child(userIdToFollow).setValue(true)
                } else {
                    val newFollowMap = mapOf(
                        userIdToFollow to true
                    )
                    dbrefFollow.child(currentUserId).setValue(newFollowMap)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // Hàm bỏ follow người dùng
    fun unfollowUser(userIdToUnfollow: String) {
        val currentUserId = userId ?: return
        dbrefFollow.child(currentUserId).child(userIdToUnfollow).removeValue()
        loadFollowedUsers()
    }

    // Hàm kiểm tra đã follow chưa
    fun isUserFollowed(userIdToCheck: String, onResult: (Boolean) -> Unit) {
        val currentUserId = userId ?: return
        dbrefFollow.child(currentUserId).child(userIdToCheck).get()
            .addOnSuccessListener { snapshot ->
                val isFollowed = snapshot.getValue(Boolean::class.java) == true
                onResult(isFollowed)
            }
    }

    // Toggle follow/unfollow
    fun toggleFollow(userIdToToggle: String) {
        val currentUserId = userId ?: return
        val followRef = dbrefFollow.child(currentUserId).child(userIdToToggle)

        followRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                unfollowUser(userIdToToggle)
            } else {
                followUser(userIdToToggle)
            }
        }
    }
}
