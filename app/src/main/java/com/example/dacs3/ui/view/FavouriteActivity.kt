package com.example.dacs3.ui.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.dacs3.data.model.OutdataSongList
import com.example.dacs3.databinding.ActivityFavouriteBinding
import com.example.dacs3.ui.adapter.FavouriteAdapter
import com.example.dacs3.ui.adapter.PlaylistChildAdapter
import com.example.dacs3.ui.viewmodel.FavouriteViewModel

class FavouriteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavouriteBinding
    private lateinit var adapter: FavouriteAdapter
    private val viewModel: FavouriteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFavouriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ẩn Action Bar
        supportActionBar?.hide()

        binding.toolbarInclude.txtTitle.text = "YÊU THÍCH"

        // Nếu cần xử lý nút back
        binding.toolbarInclude.imgBack.setOnClickListener {
            finish()
        }

        // Xử lý tìm kiếm
        binding.toolbarInclude.imgSearch.setOnClickListener {
            Toast.makeText(this, "Search clicked", Toast.LENGTH_SHORT).show()
        }

        // Quan sát LiveData từ ViewModel
        viewModel.favourites.observe(this, Observer { list ->
            if (list.isNotEmpty()) {
                // Khởi tạo Adapter và cập nhật dữ liệu vào ListView
                adapter = FavouriteAdapter(this, list)
                binding.lvMusicFav.adapter = adapter

                // Gắn sự kiện click vào từng bài hát
                adapter.setOnItemClickListener(object : FavouriteAdapter.onItemClickListener {
                    override fun onItemClick(position: Int) {
                        if (adapter.count == 0) return // chặn nếu danh sách rỗng

                        val selectedSong = adapter.getItem(position)
                        if (selectedSong != null) {
                            selectedSong.let {
                                val intent = Intent(this@FavouriteActivity, PlayerActivity::class.java)
                                intent.putExtra("image", it.image)
                                intent.putExtra("song_id", it.id)
                                intent.putExtra("audio", it.audio)
                                intent.putExtra("song_name", it.song_name)
                                startActivity(intent)
                            }
                        }else{
                            Toast.makeText(this@FavouriteActivity, "oh no", Toast.LENGTH_SHORT).show()

                        }

                    }
                })

                // Gắn sự kiện click vào nút xoá
                adapter.setOnDeleteClickListener(object : FavouriteAdapter.OnDeleteClickListener {
                    override fun onDeleteClick(position: Int) {
                        val selectedSong = adapter.getItem(position)
                        selectedSong?.let {
                            it.id?.let { songId -> viewModel.removeSongFromFavourite(songId) }

                            Toast.makeText(this@FavouriteActivity, "Đã xoá bài hát khỏi yêu thích ${it.id}", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
            } else {
                // Hiển thị thông báo nếu danh sách yêu thích trống
                binding.lvMusicFav.visibility = View.GONE
                Toast.makeText(this, "Danh sách yêu thích trống", Toast.LENGTH_SHORT).show()
            }
        })

        // Gọi hàm để lấy dữ liệu
        viewModel.loadFavourites()
    }
    override fun onResume() {
        super.onResume()
        viewModel.loadFavourites()
    }

}
