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

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class USUKMusicFragment : Fragment(R.layout.fragment_u_s_u_k_music) {
    private lateinit var lvUSUKMusic: ListView
    private lateinit var adapter: SongGerneAdapter
    private lateinit var viewModel: MainViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lvUSUKMusic = view.findViewById(R.id.lv_usuk_music)
        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        // Lấy dữ liệu bài hát thể loại Vietnam từ ViewModel
        viewModel.getSongsByCategory("USUK").observe(viewLifecycleOwner, Observer { songList ->
            // Cập nhật danh sách bài hát vào adapter
            adapter = SongGerneAdapter(requireContext(), songList)
            lvUSUKMusic.adapter = adapter
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