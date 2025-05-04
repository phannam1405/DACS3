package com.example.dacs3.ui.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.dacs3.R
import com.example.dacs3.databinding.ActivityPlaylistDadBinding
import com.example.dacs3.ui.adapter.PlaylistDadAdapter
import com.example.dacs3.ui.viewmodel.PlayListDadViewModel

class PlaylistDadActivity : AppCompatActivity() {
    // Binding cho layout Activity
    private lateinit var binding: ActivityPlaylistDadBinding

    // ViewModel quản lý dữ liệu Playlist
    private lateinit var viewModel: PlayListDadViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityPlaylistDadBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupToolbar()


        viewModel = ViewModelProvider(this).get(PlayListDadViewModel::class.java)

        // Quan sát LiveData từ ViewModel để cập nhật giao diện khi có thay đổi
        viewModel.playlist.observe(this) { playlist ->
            // Khởi tạo adapter và gán cho GridView
            val adapter = PlaylistDadAdapter(this, playlist)
            binding.gvPlaylist.adapter = adapter

            // Thiết lập các sự kiện cho các item trong danh sách
            adapter.setOnItemClickListener(object : PlaylistDadAdapter.OnItemClickListener {
                // Xử lý khi nhấn vào một playlist
                override fun onItemClick(position: Int) {
                    val playlistId = viewModel.playlist.value?.get(position)?.id
                    val playlistName = viewModel.playlist.value?.get(position)?.title
                    val intent = Intent(this@PlaylistDadActivity, PlaylistChildActivity::class.java)
                    intent.putExtra("playlistId", playlistId)
                    intent.putExtra("playlistName", playlistName)
                    startActivity(intent)
                }

                // Xử lý khi nhấn vào nút xóa playlist
                override fun onDeleteClick(position: Int) {
                    val playlistId = viewModel.playlist.value?.get(position)?.id
                    if (playlistId != null) {
                        viewModel.deletePlaylist(playlistId)  // Gọi hàm xóa playlist từ ViewModel
                        Toast.makeText(this@PlaylistDadActivity, "Playlist deleted", Toast.LENGTH_SHORT).show()
                    }
                }

                // Xử lý khi nhấn vào nút sửa tên playlist
                override fun onEditNameClick(position: Int) {
                    val currentItem = viewModel.playlist.value?.get(position)
                    currentItem?.title?.let { showEditDialog(position, it) }  // Hiển thị hộp thoại sửa tên playlist
                }
            })
        }


        binding.btnAddPlaylist.setOnClickListener {
            addNewPlaylist()
        }
    }

    private fun setupToolbar() {
        binding.toolbarInclude.txtTitle.text = "DANH SÁCH PHÁT"
        binding.toolbarInclude.imgBack.setOnClickListener {
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

    }

    // Hàm thêm playlist mới
    private fun addNewPlaylist() {
        val dialogBinding = LayoutInflater.from(this).inflate(R.layout.custom_dialog_addpl, null)
        val editTextPlaylistName = dialogBinding.findViewById<EditText>(R.id.editTextPlaylistName)
        val btnYes = dialogBinding.findViewById<Button>(R.id.btnYes)
        val btnNo = dialogBinding.findViewById<Button>(R.id.btnNo)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogBinding)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Xử lý sự kiện khi nhấn "Yes" (thêm playlist)
        btnYes.setOnClickListener {
            val playlistName = editTextPlaylistName.text.toString().trim()
            if (playlistName.isNotEmpty()) {
                viewModel.AddPlayListInFB(playlistName)
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Playlist name cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        btnNo.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    // Hàm hiển thị hộp thoại sửa tên playlist
    private fun showEditDialog(position: Int, currentTitle: String) {
        val editText = EditText(this)
        editText.setText(currentTitle)  // Đặt tên hiện tại vào EditText
        editText.hint = "Edit Playlist Name"  // Gợi ý tên mới

        // Lấy ID của playlist hiện tại
        val playlistId = viewModel.playlist.value?.get(position)?.id

        // Tạo dialog sửa tên playlist
        val dialog = AlertDialog.Builder(this)
            .setTitle("Edit Playlist Name")
            .setView(editText)
            .setPositiveButton("Save") { _, _ ->
                val newTitle = editText.text.toString().trim()
                if (newTitle.isNotEmpty()) {
                    playlistId?.let { viewModel.updatePlaylistName(it, newTitle) }  // Cập nhật tên mới vào ViewModel
                } else {
                    Toast.makeText(this, "Playlist name cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel") { dialogInterface, _ ->
                dialogInterface.dismiss()  // Đóng dialog mà không làm gì
            }
            .create()

        dialog.show()  // Hiển thị dialog
    }
}
