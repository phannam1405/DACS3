package com.example.dacs3.ui.view

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.dacs3.data.model.OutdataPlaylistDad
import com.example.dacs3.R
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

        binding.toolbarInclude.txtTitle.text = "DANH SÁCH PHÁT"

        // Nếu cần xử lý nút back
        binding.toolbarInclude.imgBack.setOnClickListener {
            finish()
        }

        // Xử lý tìm kiếm
        binding.toolbarInclude.imgSearch.setOnClickListener {
            Toast.makeText(this, "Search clicked", Toast.LENGTH_SHORT).show()
        }

        viewModel = ViewModelProvider(this).get(PlayListDadViewModel::class.java)

        // Observe data from ViewModel
        viewModel.playlist.observe(this) { playlist ->
            val adapter = PlaylistDadAdapter(this, playlist)
            binding.gvPlaylist.adapter = adapter

            adapter.setOnItemClickListener(object : PlaylistDadAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    val playlistId = viewModel.playlist.value?.get(position)?.id
                    val intent = Intent(this@PlaylistDadActivity, PlaylistChildActivity::class.java)
                    intent.putExtra("playlistId", playlistId)
                    startActivity(intent)
                }

                override fun onDeleteClick(position: Int) {
                    val playlistId = viewModel.playlist.value?.get(position)?.id
                    if (playlistId != null) {
                        viewModel.deletePlaylist(playlistId)  // Call your ViewModel method to delete
                        Toast.makeText(this@PlaylistDadActivity, "Playlist deleted", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onEditNameClick(position: Int) {
                    val currentItem = viewModel.playlist.value?.get(position)
                    if (currentItem != null) {
                        currentItem.title?.let { showEditDialog(position, it) }
                    }
                }
            })
        }

        // Add new playlist button
        binding.btnAddPlaylist.setOnClickListener {
            addNewPlaylist()
        }
    }

    private fun addNewPlaylist() {
        val editText = EditText(this)
        editText.hint = "Enter Playlist Name"

        val dialog = AlertDialog.Builder(this)
            .setTitle("Add New Playlist")
            .setView(editText)
            .setPositiveButton("Add") { _, _ ->
                val playlistName = editText.text.toString().trim()
                if (playlistName.isNotEmpty()) {
                    viewModel.AddPlayListInFB(playlistName)
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
