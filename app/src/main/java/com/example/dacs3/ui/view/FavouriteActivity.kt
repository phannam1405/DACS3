package com.example.dacs3.ui.view
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.dacs3.databinding.ActivityFavouriteBinding
import com.example.dacs3.ui.adapter.FavouriteAdapter
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

        // Khởi tạo và thiết lập Toolbar
        setupToolbar()

        // Quan sát danh sách bài hát yêu thích
        observeFavouriteSongs()

        // Khởi tạo và thiết lập Adapter cho ListView
        viewModel.loadFavourites()
    }



    private fun setupToolbar() {
        supportActionBar?.hide()
        binding.toolbarInclude.txtTitle.text = "YÊU THÍCH"
        binding.toolbarInclude.imgBack.setOnClickListener {
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }
    //Hàm quan sát danh sách bài hát yêu thích
    private fun observeFavouriteSongs() {
        viewModel.favourites.observe(this, Observer { list ->
            if (list.isNotEmpty()) {
                adapter = FavouriteAdapter(this, list)
                binding.lvMusicFav.adapter = adapter
                setupItemClickListener()
                setupDeleteClickListener()
            } else {
                binding.lvMusicFav.visibility = View.GONE
                Toast.makeText(this, "Danh sách yêu thích trống", Toast.LENGTH_SHORT).show()
            }
        })
    }





    // Hàm xử lý khi click vào item trong ListView
    private fun setupItemClickListener() {
        adapter.setOnItemClickListener(object : FavouriteAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {
                if (adapter.count == 0) return

                val selectedSong = adapter.getItem(position)
                if (selectedSong != null) {
                    val intent = Intent(this@FavouriteActivity, PlayerActivity::class.java)
                    intent.putExtra("image", selectedSong.image)
                    intent.putExtra("song_id", selectedSong.id)
                    intent.putExtra("audio", selectedSong.audio)
                    intent.putExtra("song_name", selectedSong.songName)
                    startActivity(intent)
                } else {
                    Toast.makeText(this@FavouriteActivity, "oh no", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }





    // Hàm xóa nhạc khỏi danh sách yêu thích
    private fun setupDeleteClickListener() {
        adapter.setOnDeleteClickListener(object : FavouriteAdapter.OnDeleteClickListener {
            override fun onDeleteClick(position: Int) {
                val selectedSong = adapter.getItem(position)
                selectedSong?.let {
                    it.id?.let { songId -> viewModel.removeSongFromFavourite(songId) }
                    Toast.makeText(
                        this@FavouriteActivity,
                        "Đã xoá bài hát khỏi yêu thích ${it.id}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }



    // Hàm cập nhật danh sách yêu thích
    override fun onResume() {
        super.onResume()
        viewModel.loadFavourites()
    }
}
