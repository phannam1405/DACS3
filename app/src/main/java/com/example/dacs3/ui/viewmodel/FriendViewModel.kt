package com.example.dacs3.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.dacs3.data.model.DataUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val _users = MutableLiveData<List<DataUser>>()

    private val _searchResults = MutableLiveData<List<DataUser>>()
    val searchResults: LiveData<List<DataUser>> get() = _searchResults

    private val dbref: DatabaseReference = FirebaseDatabase.getInstance(
        "https://dacs3-7408e-default-rtdb.asia-southeast1.firebasedatabase.app"
    ).getReference("User")

    init {
        layThongTinNguoiDung()
    }

    private fun layThongTinNguoiDung() {
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userList = mutableListOf<DataUser>()
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val user = userSnapshot.getValue(DataUser::class.java)
                        user?.let {
                            it.id = userSnapshot.key  // Gán id là UID từ Firebase
                            userList.add(it)
                        }
                    }
                }
                _users.postValue(userList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Lỗi lấy dữ liệu: ${error.message}")
            }
        })
    }

    fun searchUsers(query: String) {
        val queryLower = query.lowercase().trim()
        if (queryLower.isEmpty()) {
            _searchResults.postValue(emptyList())
            return
        }

        dbref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val results = mutableListOf<DataUser>()
                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(DataUser::class.java)?.apply {
                        id = userSnapshot.key
                    }

                    // Kiểm tra không phân biệt hoa thường và không dấu
                    val matches = user?.let {
                        it.userName?.lowercase()?.contains(queryLower) == true ||
                                it.phoneNumber?.lowercase()?.contains(queryLower) == true ||
                                it.email?.lowercase()?.contains(queryLower) == true
                    } ?: false

                    if (matches) {
                        user?.let { results.add(it) }
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
