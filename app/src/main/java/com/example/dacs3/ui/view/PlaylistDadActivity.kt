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
import com.example.dacs3.data.model.DataPlaylistDad
import com.example.dacs3.databinding.ActivityPlaylistDadBinding
import com.example.dacs3.ui.adapter.PlaylistDadAdapter
import com.example.dacs3.ui.viewmodel.PlayListDadViewModel

class PlaylistDadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlaylistDadBinding
    private lateinit var viewModel: PlayListDadViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityPlaylistDadBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupToolbar()
        setupViewModel()
        setupAddPlaylistButton()
    }



    // Khởi tạo và thiết lập Toolbar
    private fun setupToolbar() {
        binding.toolbarInclude.txtTitle.text = "DANH SÁCH PHÁT"
        binding.toolbarInclude.imgBack.setOnClickListener {
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }



    // Khởi tạo và thiết lập ViewModel
    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(PlayListDadViewModel::class.java)
        // Quan sát LiveData từ ViewModel để cập nhật giao diện khi có thay đổi
        viewModel.playlist.observe(this) { playlist ->
            setupPlaylistAdapter(playlist)
        }
    }



    // Thiết lập Adapter cho GridView
    private fun setupPlaylistAdapter(playlist: List<DataPlaylistDad>) {
        val adapter = PlaylistDadAdapter(this, playlist)
        binding.gvPlaylist.adapter = adapter

        adapter.setOnItemClickListener(object : PlaylistDadAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                navigateToOpenlistChild(position)
            }

            override fun onDeleteClick(position: Int) {
                deletePlaylist(position)
            }

            override fun onEditNameClick(position: Int) {
                editPlaylistName(position)
            }
        })
    }



    // Chuyển hướng đến Activity con
    private fun navigateToOpenlistChild(position: Int) {
        val playlistId = viewModel.playlist.value?.get(position)?.id
        val playlistName = viewModel.playlist.value?.get(position)?.title
        val intent = Intent(this@PlaylistDadActivity, PlaylistChildActivity::class.java)
        intent.putExtra("playlistId", playlistId)
        intent.putExtra("playlistName", playlistName)
        startActivity(intent)
    }



    // Xóa Playlist
    private fun deletePlaylist(position: Int) {
        val playlistId = viewModel.playlist.value?.get(position)?.id
        playlistId?.let {
            viewModel.deletePlaylist(it)
            Toast.makeText(this@PlaylistDadActivity, "Playlist deleted", Toast.LENGTH_SHORT).show()
        }
    }



    // Sửa tên Playlist
    private fun editPlaylistName(position: Int) {
        val currentItem = viewModel.playlist.value?.get(position)
        currentItem?.title?.let { showEditDialog(position, it) }
    }


    // Thêm Playlist mới
    private fun setupAddPlaylistButton() {
        binding.btnAddPlaylist.setOnClickListener {
            addNewPlaylist()
        }
    }


    // Thêm Playlist mới
    private fun addNewPlaylist() {
        val dialogBinding = LayoutInflater.from(this).inflate(R.layout.custom_dialog_addpl, null)
        val editTextPlaylistName = dialogBinding.findViewById<EditText>(R.id.editTextPlaylistName)
        val btnYes = dialogBinding.findViewById<Button>(R.id.btnYes)
        val btnNo = dialogBinding.findViewById<Button>(R.id.btnNo)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogBinding)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

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



    // Hiển thị Dialog để sửa tên Playlist
    private fun showEditDialog(position: Int, currentTitle: String) {
        val editText = EditText(this)
        editText.setText(currentTitle)
        editText.hint = "Edit Playlist Name"

        val playlistId = viewModel.playlist.value?.get(position)?.id

        val dialog = AlertDialog.Builder(this)
            .setTitle("Edit Playlist Name")
            .setView(editText)
            .setPositiveButton("Save") { _, _ ->
                val newTitle = editText.text.toString().trim()
                if (newTitle.isNotEmpty()) {
                    playlistId?.let { viewModel.updatePlaylistName(it, newTitle) }
                } else {
                    Toast.makeText(this, "Playlist name cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .create()

        dialog.show()
    }
}
