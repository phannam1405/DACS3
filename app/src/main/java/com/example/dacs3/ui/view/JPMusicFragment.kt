package com.example.dacs3.ui.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.dacs3.R
import com.example.dacs3.ui.adapter.SongGerneAdapter
import com.example.dacs3.ui.viewmodel.MainViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class JPMusicFragment : Fragment(R.layout.fragment_j_p_music) {
    private lateinit var lvJpMusic: ListView
    private lateinit var adapter: SongGerneAdapter
    private lateinit var viewModel: MainViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lvJpMusic = view.findViewById(R.id.lv_jp_music)
        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        // Lấy dữ liệu bài hát thể loại Vietnam từ ViewModel
        viewModel.getSongsByCategory("Japanese").observe(viewLifecycleOwner, Observer { songList ->
            // Cập nhật danh sách bài hát vào adapter
            adapter = SongGerneAdapter(requireContext(), songList)
            lvJpMusic.adapter = adapter
        })
    }

}