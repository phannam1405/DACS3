package com.example.dacs3.ui.view

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.dacs3.R
import com.example.dacs3.data.model.DataSongList
import com.example.dacs3.ui.adapter.SongGerneAdapter
import com.example.dacs3.ui.viewmodel.MainViewModel

class CNMusicFragment : Fragment(R.layout.fragment_c_n_music) {
    private lateinit var lvCnMusic: ListView
    private lateinit var adapter: SongGerneAdapter
    private lateinit var viewModel: MainViewModel
    private lateinit var songList: List<DataSongList>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lvCnMusic = view.findViewById(R.id.lv_cn_music)
        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        // Lấy dữ liệu bài hát thể loại Vietnam từ ViewModel
        viewModel.getSongsByCategory("Chinese").observe(viewLifecycleOwner, Observer { songList ->
            adapter = SongGerneAdapter(requireContext(), songList)
            lvCnMusic.adapter = adapter
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