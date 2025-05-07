package com.example.dacs3.ui.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.dacs3.R
import com.example.dacs3.data.model.DataSongList
import com.example.dacs3.ui.adapter.SongGerneAdapter
import com.example.dacs3.ui.viewmodel.MainViewModel

class VNMusicFragment : Fragment(R.layout.fragment_v_n_music) {
    private lateinit var lvVnMusic: ListView
    private lateinit var adapter: SongGerneAdapter
    private lateinit var viewModel: MainViewModel


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lvVnMusic = view.findViewById(R.id.lv_vn_music)
        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        // Lấy dữ liệu bài hát thể loại Vietnam từ ViewModel
        viewModel.getSongsByCategory("Vietnam").observe(viewLifecycleOwner, Observer { songList ->
            adapter = SongGerneAdapter(requireContext(), songList)
            lvVnMusic.adapter = adapter

            adapter.setOnItemClickListener(object : SongGerneAdapter.OnItemClickListener {
                override fun onItemClick(song: DataSongList) {
                    val intent = Intent(requireContext(), PlayerActivity::class.java).apply {
                        putExtra("image", song.image)
                        putExtra("song_id", song.id)
                        putExtra("audio", song.audio)
                        putExtra("song_name", song.songName)
                        putExtra("song", song)
                        putExtra("song_list", ArrayList(songList))

                    }
                    startActivity(intent)
                }
            })
        })
    }}