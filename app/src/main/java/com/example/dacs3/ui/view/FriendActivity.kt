package com.example.dacs3.ui.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dacs3.R
import com.example.dacs3.databinding.ActivityFriendBinding
import com.example.dacs3.ui.adapter.FollowAdapter
import com.example.dacs3.ui.adapter.UserAdapter
import com.example.dacs3.ui.viewmodel.FollowViewModel
import com.example.dacs3.ui.viewmodel.FriendViewModel


class FriendActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFriendBinding
    private lateinit var viewModel: FriendViewModel
    private lateinit var followViewModel: FollowViewModel
    private lateinit var searchAdapter: UserAdapter
    private lateinit var followAdapter: FollowAdapter

    private var isSearchMode = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Khởi tạo ViewModel
        viewModel = ViewModelProvider(this).get(FriendViewModel::class.java)
        followViewModel = ViewModelProvider(this).get(FollowViewModel::class.java)

        // Cài đặt Adapter cho cả 2 RecyclerView
        setupSearchAdapter()
        setupFollowAdapter()

        // Quan sát thay đổi danh sách người theo dõi
        followViewModel.followedUsers.observe(this) { users ->
            followAdapter.list = users
            followAdapter.notifyDataSetChanged()
        }

        // Quan sát kết quả tìm kiếm
        observeSearchResults()

        // Cài đặt sự kiện cho Bottom Navigation
        setupBottomNavigation()

        // Cài đặt sự kiện cho SearchView
        setupSearchView()
    }

    private fun setupSearchAdapter() {
        // Adapter cho kết quả tìm kiếm
        searchAdapter = UserAdapter(
            onItemClick = { user ->
                val intent = Intent(this, ProfileActivity::class.java).apply {
                    putExtra("user_id", user.id)
                    putExtra("user_name", user.userName)
                    putExtra("user_email", user.email)
                    putExtra("user_phone", user.phoneNumber)
                    putExtra("user_avatar", user.avatarUrl)
                    putExtra("is_editable", false)
                }
                startActivity(intent)
                exitSearchMode()
            },
            onFollowClick = { user ->
                user.id?.let { userId -> followViewModel.followUser(userId) }
            }
        )
        binding.rVSearch.adapter = searchAdapter
        binding.rVSearch.layoutManager = LinearLayoutManager(this)
    }

    private fun setupFollowAdapter() {
        followAdapter = FollowAdapter(this, mutableListOf())

        binding.listFollow.adapter = followAdapter

        // Thêm đoạn này để xử lý click nút unfollow
        followAdapter.setOnFollowClickListener(object : FollowAdapter.OnFollowClickListener {
            override fun onFollowClick(position: Int) {
                val user = followAdapter.list[position]
                user.id?.let { userId ->
                    followViewModel.unfollowUser(userId)
                }
            }
        })


        // Thêm đoạn này nếu muốn click vào item mở profile người dùng
        followAdapter.setOnItemClickListener(object : FollowAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val user = followAdapter.list[position]
                val intent = Intent(this@FriendActivity, ProfileActivity::class.java).apply {
                    putExtra("user_id", user.id)
                    putExtra("user_name", user.userName)
                    putExtra("user_email", user.email)
                    putExtra("user_phone", user.phoneNumber)
                    putExtra("user_avatar", user.avatarUrl)
                    putExtra("is_editable", false)
                }
                startActivity(intent)
            }
        })
    }


    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.itemFav -> {
                    startActivity(Intent(this, FavouriteActivity::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    true
                }
                R.id.itemFriend -> true
                R.id.itemHome -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    true
                }
                R.id.itemPlaylist -> {
                    startActivity(Intent(this, PlaylistDadActivity::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    true
                }
                else -> false
            }
        }
    }

    private fun setupSearchView() {
        val searchView = binding.customToolbar.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.takeIf { it.isNotEmpty() }?.let {
                    enterSearchMode()
                    viewModel.searchUsers(it)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    if (it.isNotEmpty()) {
                        enterSearchMode()
                        viewModel.searchUsers(it)
                    } else {
                        exitSearchMode()
                    }
                }
                return false
            }
        })
    }

    private fun observeSearchResults() {
        viewModel.searchResults.observe(this) { results ->
            if (isSearchMode) {
                searchAdapter.updateData(results)
            }
        }
    }

    private fun enterSearchMode() {
        isSearchMode = true
        binding.rVSearch.visibility = View.VISIBLE
        binding.listFollow.visibility = View.GONE
    }

    private fun exitSearchMode() {
        isSearchMode = false
        binding.rVSearch.visibility = View.GONE
        binding.listFollow.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        binding.bottomNavigation.selectedItemId = R.id.itemFriend
        followViewModel.loadFollowedUsers()
    }
}
